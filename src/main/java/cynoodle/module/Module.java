/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.module;

import com.google.common.flogger.FluentLogger;
import cynoodle.CyNoodle;
import cynoodle.util.text.ANSIColors;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>A module in cynoodle.</p>
 *
 * <p>Modules are identified by module identifiers, strings in the form
 * of <code>group:name</code> (see {@link ModuleIdentifier}) and registered in
 * the cynoodle {@link ModuleManager} / {@link ModuleRegistry}.</p>
 *
 * <p>They are a useful way to organize and group different features together.</p>
 *
 * <p>Modules also automatically act as a listener for events, which means that
 * event handler methods can be directly defined.</p>
 *
 * @see ModuleIdentifier
 * @see ModuleDescriptor
 */
public abstract class Module {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // === INTERNAL ===

    private CyNoodle noodle;

    private ModuleManager manager;

    private ModuleIdentifier identifier;

    // ===

    private ModuleDescriptor descriptor;

    // ===

    protected Module() {}

    // === INTERNAL ===

    /**
     * Internally initialize this module instance.
     */
    final void init(@Nonnull ModuleManager manager, @Nonnull ModuleIdentifier identifier) {
        this.noodle = CyNoodle.get();
        this.manager = manager;
        this.identifier = identifier;
    }

    // ===

    /**
     * Get the cynoodle application instance.
     * @return the cynoodle instance
     */
    @Nonnull
    public final CyNoodle noodle() {
        return this.noodle;
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

    // ===

    /**
     * Check if this Module is an internal Module.
     * @return true if it is, false if not
     * @see ModuleDescriptor#isInternal() ()
     */
    public final boolean isInternal() {
        return getDescriptor().isInternal();
    }

    // ===

    /**
     * Get a set of all effective module dependencies this Module has.
     * This is all declared dependencies and, if this is not a System Module itself, all
     * System Modules.
     * @return a set of effective dependency Module identifiers.
     */
    @Nonnull
    public final Set<ModuleIdentifier> getEffectiveDependencies() {

        Set<ModuleIdentifier> explicitDependencies = getDescriptor().getDependencies();

        Set<ModuleIdentifier> dependencies = new HashSet<>(explicitDependencies);

        // non-internal modules automatically depend on all system modules
        if(!this.isInternal()) {

            Set<ModuleIdentifier> systemModules = this.manager.getRegistry().all()
                    .filter(Module::isInternal).map(Module::getIdentifier)
                    .collect(Collectors.toCollection(HashSet::new));

            dependencies.addAll(systemModules);
        }

        LOG.atFiner().log("Found effective dependencies on %s: %s", this.getIdentifier(), dependencies);

        return Collections.unmodifiableSet(dependencies);
    }

    // ======

    /**
     * Start the Module, to be overridden by the implementation.
     * The default implementation includes only logging.
     */
    // TODO document exceptions
    @OverridingMethodsMustInvokeSuper
    protected void start() {
        LOG.atInfo().log("Starting " + ANSIColors.colored("%s", ANSIColors.CYAN), this.identifier);
    }

    /**
     * Shutdown the Module, to be overridden by the implementation.
     * The default implementation includes only logging.
     */
    // TODO document exceptions
    @OverridingMethodsMustInvokeSuper
    protected void shutdown() {
        LOG.atInfo().log("Shutting down " + ANSIColors.colored("%s", ANSIColors.CYAN), this.identifier);
    }

    //

    /**
     * Called after the Module and all its dependencies have been started,
     * to be overridden by the implementation.
     */
    protected void afterStart() {
        //
    }

    /**
     * Called before the Module and all its dependencies have been shutdown,
     * to be overridden by the implementation.
     */
    protected void beforeShutdown() {
        //
    }

    // ======

    @Override
    public String toString() {
        return getIdentifier().toString();
    }

    // === UTILITIES ===

    /**
     * Register the given listener in the event bus.
     * @param listeners the listener to register
     */
    protected final void registerListener(@Nonnull Object listeners) {
        this.noodle.getEvents()
                .register(listeners);
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

    // ===

    @Nonnull
    static Module create(@Nonnull ModuleManager manager, @Nonnull ModuleDescriptor descriptor) {
        return descriptor.create(manager);
    }
}