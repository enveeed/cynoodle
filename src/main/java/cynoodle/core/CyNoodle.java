/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import com.google.common.eventbus.EventBus;
import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.Snowflake;
import cynoodle.core.base.ac.ACModule;
import cynoodle.core.base.command.CommandModule;
import cynoodle.core.base.condition.ConditionModule;
import cynoodle.core.base.fm.FMModule;
import cynoodle.core.base.localization.LocalizationModule;
import cynoodle.core.base.profile.ProfileModule;
import cynoodle.core.base.strikes.StrikesModule;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.discord.DiscordModule;
import cynoodle.core.module.ModuleClassException;
import cynoodle.core.module.ModuleManager;
import cynoodle.core.mongo.MongoModule;
import enveeed.carambola.Carambola;
import enveeed.carambola.CarambolaConfiguration;
import sun.misc.Signal;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * cynoodle-core main class
 */
public final class CyNoodle {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // === STATICS ===

    /**
     * cynoodle zero timestamp 2018-01-01 00:00:00 UTC
     */
    public static final Instant CYNOODLE_EPOCH = Instant.ofEpochMilli(1514764800000L);

    /**
     * discord zero timestamp - source: JDA MiscUtil
     */
    public static final Instant DISCORD_EPOCH = Instant.ofEpochMilli(1420070400000L);

    /**
     * Database name for all cynoodle MongoDB collections (possibly temporary).
     */
    public static final String DB_NAME = "cynoodle";

    // === INSTANCE ===

    /**
     * The global cynoodle instance
     */
    private static CyNoodle noodle = null;

    // === INTERNALS ===

    private final LaunchSettings launchSettings;

    // ======

    private Configuration configuration;

    private final ModuleManager modules = new ModuleManager();

    // TODO replace Guava EventBus with a more specific self-made implementation for cynoodle
    private final EventBus events = new EventBus("cynoodle");

    // ======

    private Snowflake snowflake;

    // ======

    /**
     * internal cynoodle instance constructor, only called once during initialization.
     * @param settings the cynoodle launch settings to use
     */
    private CyNoodle(@Nonnull LaunchSettings settings) {
        if(CyNoodle.noodle != null) throw new Error();
        this.launchSettings = settings;
    }

    // ======

    public static void launch(@Nonnull LaunchSettings settings) throws IllegalStateException {
        if(noodle != null) throw new IllegalStateException("cynoodle was already launched.");

        // set up logging

        Carambola carambola = Carambola.get();
        CarambolaConfiguration configuration = carambola.getConfiguration();

        configuration.setMinimumLevel(Level.INFO.intValue() - 1);

        // create the cynoodle instance
        noodle = new CyNoodle(settings);

        // create the root thread and start it to start cynoodle
        Thread root = new Thread(noodle::run, "root");

        root.start();
    }

    // === LIFECYCLE ===

    /**
     * root thread lifecycle method,
     * starts cynoodle and then stays in parking state until shutdown and stops cynoodle.
     *
     * Never returns as its halting the JVM at its end.
     */
    private void run() {

        this.rootThread = Thread.currentThread();

        //

        int status = 0;

        try {

            start();        // start cynoodle

            parkRoot();     // stay parked until shutdown

            stop();         // stop cynoodle

        } catch (StartException startEx) {
            LOG.atSevere()
                    .withCause(startEx)
                    .log("Start could not be completed successfully: %s", startEx.getMessage());
            status = 1;

        } catch (StopException stopEx) {
            LOG.atSevere()
                    .withCause(stopEx)
                    .log("Stop could not be completed successfully: %s", stopEx.getMessage());
            status = 1;
        } catch (Exception ex) {
            LOG.atSevere()
                    .withCause(ex)
                    .log("Unexpected exception in root thread: %s", ex.getMessage());
            ex.printStackTrace();
            status = 1;
        }

        //

        Runtime.getRuntime().halt(status);
    }

    //

    /**
     * Start cynoodle.
     * @throws StartException if the start could not be completed successfully
     */
    private void start() throws StartException {

        LOG.atInfo().log("Starting cynoodle %s ...", BuildConfig.VERSION);

        // === CONFIGURATION ===

        try {
            this.configuration = Configuration.read(Paths.get("./config.json"));
        } catch (IOException e) {
            throw new StartException(e);
        }

        // ===

        this.snowflake = Snowflake.get(0L, CYNOODLE_EPOCH); // TODO with machine ID

        // === MODULES ===

        try {
            this.registerSystemModules();
            this.registerBaseModules();
        } catch (ModuleClassException ex) {
            throw new StartException(ex);
        }

        this.modules.registerEventListeners(this.events);

        //

        try {
            this.modules.start();
        } catch (Exception ex) {
            throw new StartException(ex);
        }

        // ===

        this.setupInterruptionHandler();

        // ===

        this.events.post(new StartEvent());

        // ===

        LOG.atInfo().log("Started.");
    }

    /**
     * Stop cynoodle.
     * @throws StopException if the stop could not be completed successfully
     */
    private void stop() throws StopException {

        LOG.atInfo().log("Stopping cynoodle ...");

        // ===

        this.events.post(new StopEvent());

        // === DISCORD ===

        LOG.atInfo().log("Stopping Modules ...");

        this.modules.shutdown();

        // ===

        LOG.atInfo().log("Stopped.");
    }

    // === SHUTDOWN ===

    // TODO! check state and stuff, prevent bad callers from doing this randomly
    public void shutdown() {

        LOG.atInfo().log("Signalling root thread un-parking for shutdown");

        signalUnparkRoot();
    }

    // === ROOT THREAD MANAGEMENT ===

    private Thread rootThread = null;

    private final ReentrantLock rootParkingLock = new ReentrantLock();
    private final Condition rootParkingCondition = rootParkingLock.newCondition();

    private boolean rootParked = false;

    //

    /**
     * Signal that the root thread should be un-parked and
     * {@link #parkRoot()} shall return for the root thread.
     *
     * (called by any thread)
     */
    private void signalUnparkRoot() {

        if(!rootParked) throw new IllegalStateException("Root thread is not parked and cannot be un-parked!");

        rootParkingLock.lock();

        try {
            rootParked = false;
            rootParkingCondition.signal();
        } finally {
            rootParkingLock.unlock();
        }
    }

    /**
     * Enter the parking state for the root thread.
     * Does not return until un-parking was signalled.
     *
     * (called by the root thread)
     */
    private void parkRoot() {

        rootParkingLock.lock();

        try {
            rootParked = true;
            while (rootParked) rootParkingCondition.awaitUninterruptibly();
        } finally {
            rootParkingLock.unlock();
        }
    }

    //

    /**
     * Determine if the caller thread is the root thread.
     * @return true if the thread calling this method is the root thread, false otherwise
     */
    private boolean isCallerRootThread() {
        return Thread.currentThread() == this.rootThread;
    }

    // ===

    /**
     * Register all system modules (module group "<code>system</code>").
     */
    // TODO temporary
    private void registerSystemModules() throws ModuleClassException {

        this.modules.register(MongoModule.class);           // base:mongo
        this.modules.register(DiscordModule.class);         // base:discord

    }

    /**
     * Register all base modules (module group "<code>base</code>").
     */
    // TODO temporary
    private void registerBaseModules() throws ModuleClassException {

        this.modules.register(CommandModule.class);         // base:command
        this.modules.register(LocalizationModule.class);    // base:localization
        this.modules.register(StrikesModule.class);         // base:strikes
        this.modules.register(XPModule.class);              // base:xp
        this.modules.register(FMModule.class);              // base:fm
        this.modules.register(ProfileModule.class);         // base:profile
        this.modules.register(ConditionModule.class);       // base:condition
        this.modules.register(ACModule.class);              // base:ac
    }

    // ===

    /**
     * Add a shutdown hook to gracefully shutdown cynoodle in case of a SIGINT.
     */
    private void setupInterruptionHandler() {

        // SIGINT / CTRL + C
        Signal.handle(new Signal("INT"), s -> shutdown());

        // SIGTERM (before shutdown)
        Signal.handle(new Signal("TERM"), s -> shutdown()); // TODO shutdownFast() ?

    }

    // ======

    /**
     * Get the cyborgnoodle launch settings.
     * @return the launch settings
     */
    @Nonnull
    public LaunchSettings getLaunchSettings() {
        return this.launchSettings;
    }

    // ======

    @Nonnull
    public ModuleManager getModules() {
        return modules;
    }

    @Nonnull
    public EventBus getEvents() {
        return events;
    }

    // ======

    @Nonnull
    public DiscordModule getDiscord() {
        return this.modules.getRegistry()
                .get(DiscordModule.class)
                .orElseThrow(IllegalStateException::new);
    }

    @Nonnull
    public MongoModule getMongo() {
        return this.modules.getRegistry()
                .get(MongoModule.class)
                .orElseThrow(IllegalStateException::new);
    }

    // ======

    @Nonnull
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Nonnull
    public Snowflake getSnowflake() {
        return this.snowflake;
    }

    // ======

    /**
     * Obtain the global cyborgnoodle core instance. Must be launched via {@link #launch(LaunchSettings)} first.
     * @return the global cyborgnoodle core instance
     * @throws IllegalStateException if cyborgnoodle was not launched yet, this should never be handled
     */
    @Nonnull
    public static CyNoodle get() throws IllegalStateException {
        if(noodle == null)
            throw new IllegalStateException("cynoodle was not launched yet! (see CyNoodle.launch())");
        return noodle;
    }
}
