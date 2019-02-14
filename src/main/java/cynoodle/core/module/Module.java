/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.module;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.CyNoodle;
import cynoodle.core.api.output.ANSIColors;
import cynoodle.core.events.EventListener;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.NoSuchElementException;

/**
 * <p>A module in cynoodle.</p>
 *
 * <p>Modules are identified by module identifiers, strings in the form
 * of <code>group:name</code> (see {@link ModuleIdentifier}) and registered in
 * the cynoodle {@link ModuleManager} / {@link ModuleRegistry}.</p>
 *
 * <p>They are a useful way to organize and group different features together.</p>
 *
 * <p>Modules also automatically act as {@link EventListener} instances, which means that
 * event handler methods can be directly defined.</p>
 *
 * @see ModuleIdentifier
 * @see ModuleDescriptor
 */
public abstract class Module implements EventListener {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // === INTERNAL ===

    private ModuleManager manager;

    private ModuleIdentifier identifier;

    // ===

    protected Module() {}

    // === INTERNAL ===

    /**
     * Internally initialize this module instance.
     */
    final void init(@Nonnull ModuleManager manager, @Nonnull ModuleIdentifier identifier) {
        this.manager = manager;
        this.identifier = identifier;
    }

    // ===

    /**
     * Get the module manager which manages this module.
     * @return the module manager.
     */
    @Nonnull
    public final ModuleManager getManager() {
        return this.manager;
    }

    //

    /**
     * Get the identifier for this module.
     * @return the module identifier
     */
    @Nonnull
    public final ModuleIdentifier getIdentifier() {
        return this.identifier;
    }

    /**
     * Get the descriptor for this module.
     * @return the module descriptor
     */
    @Nonnull
    public final ModuleDescriptor getDescriptor() {
        return this.manager.getRegistry().getDescriptor(this.identifier)
                .orElseThrow(IllegalStateException::new);
    }

    // ======

    /**
     * Start the Module, to be overridden by the implementation.
     * The default implementation includes only logging.
     */
    // TODO document exceptions
    @OverridingMethodsMustInvokeSuper
    protected void start() {
        LOG.atInfo().log("Starting " + ANSIColors.CYAN + "%s" + ANSIColors.RESET, this.identifier);
    }

    /**
     * Shutdown the Module, to be overridden by the implementation.
     * The default implementation includes only logging.
     */
    // TODO document exceptions
    @OverridingMethodsMustInvokeSuper
    protected void shutdown() {
        LOG.atInfo().log("Shutting down " + ANSIColors.CYAN + "%s" + ANSIColors.RESET, this.identifier);
    }

    // ======

    @Override
    public String toString() {
        return getIdentifier().toString();
    }

    // ======

    /**
     * Get a module by the given identifier from the cynoodle module manager ({@link CyNoodle#getModules()}).
     * @param identifier the identifier string
     * @return a module, if found
     * @throws NoSuchElementException if no module could be found with the given identifier
     */
    @Nonnull
    public static Module get(@Nonnull String identifier) throws NoSuchElementException {
        return CyNoodle.get()
                .getModules().getRegistry()
                .get(identifier).orElseThrow();
    }

    /**
     * Get a module by the given identifier from the cynoodle module manager ({@link CyNoodle#getModules()}).
     * @param identifier the identifier
     * @return a module, if found
     * @throws NoSuchElementException if no module could be found with the given identifier
     */
    @Nonnull
    public static Module get(@Nonnull ModuleIdentifier identifier) throws NoSuchElementException {
        return CyNoodle.get()
                .getModules().getRegistry()
                .get(identifier).orElseThrow();
    }

    // TODO docs
    @Nonnull
    public static <M extends Module> M get(@Nonnull Class<M> moduleClass) throws NoSuchElementException {
        return CyNoodle.get()
                .getModules().getRegistry()
                .get(moduleClass).orElseThrow();
    }
}