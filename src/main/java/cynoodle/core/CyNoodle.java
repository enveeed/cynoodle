/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import com.google.common.eventbus.EventBus;
import com.google.common.flogger.FluentLogger;
import com.mongodb.client.MongoClient;
import cynoodle.core.base.TestModule;
import cynoodle.core.base.command.CommandModule;
import cynoodle.core.base.strikes.StrikesModule;
import cynoodle.core.discord.DiscordModule;
import cynoodle.core.module.ModuleClassException;
import cynoodle.core.module.ModuleManager;
import cynoodle.core.mongo.MongoModule;
import net.dv8tion.jda.bot.sharding.ShardManager;
import sun.misc.Signal;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    // === INSTANCE ===

    /**
     * The global cynoodle instance
     */
    private static CyNoodle noodle = null;

    // === INTERNALS ===

    private final StartParameters parameters;

    // ======

    private final ModuleManager modules = new ModuleManager();

    // TODO replace Guava EventBus with a more specific self-made implementation for cynoodle
    private final EventBus events = new EventBus("cynoodle");

    // ======

    private ShardManager discord;
    private MongoClient mongo;

    //

    private Snowflake snowflake;

    //

    private Scheduler scheduler;
    private Console console;

    // ======

    /**
     * internal cynoodle instance constructor, only called once during initialization.
     * @param parameters the cynoodle start parameters to use
     */
    private CyNoodle(@Nonnull StartParameters parameters) {
        if(CyNoodle.noodle != null) throw new Error();
        this.parameters = parameters;
    }

    // ======

    public static void launch(@Nonnull StartParameters configuration) throws IllegalStateException {
        if(noodle != null) throw new IllegalStateException("cynoodle was already launched.");

        // create the cynoodle instance
        noodle = new CyNoodle(configuration);

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

    // TODO temp
    private void registerSystemModules() throws ModuleClassException {
        this.modules.register(MongoModule.class);
        this.modules.register(DiscordModule.class);
    }

    /**
     * Register all base modules (module group "<code>base</code>")
     */
    // TODO temp
    private void registerBaseModules() throws ModuleClassException {

        // TODO

        this.modules.register(CommandModule.class);

        this.modules.register(StrikesModule.class);


        this.modules.register(TestModule.class); //TODO test only
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
     * Get the cyborgnoodle start parameters.
     * @return the start parameters
     */
    @Nonnull
    public StartParameters getParameters() {
        return parameters;
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
    public Snowflake getSnowflake() {
        return this.snowflake;
    }

    // ======

    @Nonnull
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Nonnull
    public Console getConsole() {
        return console;
    }

    // ======

    // ======

    /**
     * Obtain the global cyborgnoodle core instance. Must be launched via {@link #launch(StartParameters)} first.
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
