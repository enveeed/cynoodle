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

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.flogger.FluentLogger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.util.algorithm.Sorter;
import cynoodle.util.algorithm.Sorters;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages all modules in cynoodle.
 */
// TODO this class is a train wreck fucking hell
// TODO completely improve this, duplicate code, saving the start order, actually checking that the deps exist, immutable registry, etc ...
public final class ModuleManager {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final ModuleRegistry registry = new ModuleRegistry(this);

    //

    private final Sorter<ModuleIdentifier> sorter =
            Sorters.topological(identifier -> registry.get(identifier).orElseThrow().getEffectiveDependencies());

    // ===

    @Nonnull
    public ModuleRegistry getRegistry() {
        return this.registry;
    }

    // ===

    @CanIgnoreReturnValue
    @Nonnull
    public ModuleIdentifier register(@Nonnull Class<? extends Module> moduleClass) throws ModuleClassException {
        return this.registry.register(moduleClass);
    }

    // === DEPENDENCIES ===

    private void checkDependenciesExist() throws ModuleDependencyException {

        List<Module> modules = this.registry.all().collect(Collectors.toList());

        for (Module module : modules) {
            Set<ModuleIdentifier> dependencies = module.getDescriptor().getDependencies();

            for (ModuleIdentifier dependency : dependencies)
                this.registry.get(dependency)
                        .orElseThrow(() -> new ModuleDependencyException("Module " + module + " is missing dependency " + dependency + "!"));
        }
    }

    // === START / SHUTDOWN ===

    // TODO exceptions (!!!)
    public void start() throws Exception {

        this.checkDependenciesExist();

        // collect module identifier list
        List<ModuleIdentifier> modules = this.registry.allIdentifiers().collect(Collectors.toList());

        // try to sort the modules for starting
        List<ModuleIdentifier> sorted;
        try {
            sorted = this.sorter.sort(modules);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Failed to determine module start order, " +
                    "there are cycles in the registered Module dependencies!", ex);
        }

        // start the modules

        for (ModuleIdentifier identifier : sorted) {

            Module module = this.registry.get(identifier).orElseThrow(IllegalStateException::new);

            try {
                module.start();
            } catch (Exception ex) {
                throw new Exception("Failed to start Module " + module + "!", ex);
            }
        }

        // call after start triggers

        for (ModuleIdentifier identifier : sorted) {

            Module module = this.registry.get(identifier).orElseThrow(IllegalStateException::new);

            try {
                module.afterStart();
            } catch (Exception ex) {
                throw new Exception("Failed to call afterStart() on Module " + module + "!", ex);
            }
        }

        // all modules started
    }

    // TODO exceptions (!!!)
    public void shutdown() {

        // collect module identifier list
        List<ModuleIdentifier> modules = this.registry.allIdentifiers().collect(Collectors.toList());

        // try to sort the modules for stopping
        List<ModuleIdentifier> sorted;
        try {
            sorted = new ArrayList<>(this.sorter.sort(modules));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Failed to determine module stop order, " +
                    "there are cycles in the registered Module dependencies!", ex);
        }

        Lists.reverse(sorted);

        // call before stop triggers

        for (ModuleIdentifier identifier : sorted) {

            Module module = this.registry.get(identifier).orElseThrow(IllegalStateException::new);

            try {
                module.beforeShutdown();
            } catch (Exception ex) {
                // only log this, dont throw an exception cause we want at least the other modules to stop normally
                LOG.atSevere().withCause(ex).log("Failed to call beforeShutdown() on Module %s!", module);
            }
        }

        // stop the modules

        for (ModuleIdentifier identifier : sorted) {

            Module module = this.registry.get(identifier).orElseThrow(IllegalStateException::new);

            try {
                module.shutdown();
            } catch (Exception ex) {
                // only log this, dont throw an exception cause we want at least the other modules to stop normally
                LOG.atSevere().withCause(ex).log("Failed to stop Module %s!", module);
            }
        }
    }

    //

    /**
     * Register all currently registered modules as event listeners with the given event bus.
     * @param bus the event bus
     */
    public void registerEventListeners(@Nonnull EventBus bus) {
        this.registry.all().forEach(bus::register);
    }
}