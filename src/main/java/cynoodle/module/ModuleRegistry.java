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

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Registry for all modules in cynoodle, used within {@link ModuleManager}.
 */
public final class ModuleRegistry {

    private final ModuleManager manager;

    // ===

    private final Map<ModuleIdentifier, Module> modules = new HashMap<>();

    private final Map<ModuleIdentifier, ModuleDescriptor> descriptors = new HashMap<>();

    //

    private final Map<Class<? extends Module>, ModuleIdentifier> classes = new HashMap<>();

    // ===

    ModuleRegistry(@Nonnull ModuleManager manager) {
        this.manager = manager;
    }

    // ===

    @CanIgnoreReturnValue
    @Nonnull
    ModuleIdentifier register(@Nonnull Class<? extends Module> moduleClass) throws ModuleClassException {

        // parse module class
        ModuleDescriptor descriptor = ModuleDescriptor.parse(moduleClass);

        // create module instance (already initialized by create())
        Module module = descriptor.create(this.manager);

        // register the module
        ModuleIdentifier identifier = descriptor.getIdentifier();

        this.modules.put(identifier, module);
        this.descriptors.put(identifier, descriptor);

        this.classes.put(moduleClass, identifier);

        return identifier;
    }

    // ===

    @Nonnull
    public Optional<Module> get(@Nonnull ModuleIdentifier identifier) {
        return Optional.ofNullable(this.modules.get(identifier));
    }

    //

    @Nonnull
    public Optional<Module> get(@Nonnull String identifier) throws IllegalArgumentException {
        return get(ModuleIdentifier.parse(identifier));
    }

    @Nonnull
    public Optional<Module> get(@Nonnull String group, @Nonnull String name) throws IllegalArgumentException {
        return get(ModuleIdentifier.of(group, name));
    }

    //

    @Nonnull
    public <M extends Module> Optional<M> get(@Nonnull Class<M> moduleClass) throws IllegalArgumentException {

        ModuleIdentifier identifier = this.classes.get(moduleClass);
        if(identifier == null) return Optional.empty();

        // throw because if the identifier exists, this should exist as well
        Module module = get(identifier).orElseThrow(IllegalStateException::new);

        if(module.getDescriptor().getModuleClass() != moduleClass)
            throw new IllegalArgumentException("Module class mismatch!");

        // cast and return as optional
        return Optional.of(moduleClass.cast(module));
    }

    // ===

    /**
     * Return a stream of all registered modules.
     * @return a stream containing the registered modules
     */
    @Nonnull
    public Stream<Module> all() {
        return this.modules.values().stream();
    }

    //

    @Nonnull
    public Stream<Module> allByGroup(@Nonnull String group) {
        return all().filter(module -> module.getIdentifier().getGroup().equals(group));
    }

    @Nonnull
    public Stream<ModuleIdentifier> allIdentifiers() {
        return all().map(Module::getIdentifier);
    }

    // ===

    @Nonnull
    Optional<ModuleDescriptor> getDescriptor(@Nonnull ModuleIdentifier identifier) {
        return Optional.ofNullable(this.descriptors.get(identifier));
    }
}
