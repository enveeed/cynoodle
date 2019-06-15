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
import cynoodle.util.reflect.Annotations;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Descriptor for a {@link Module} class.
 */
public final class ModuleDescriptor {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final Class<? extends Module> moduleClass;

    // ===

    private final ModuleIdentifier identifier;
    private final Set<ModuleIdentifier> dependencies;
    private final boolean system;

    // ===

    private ModuleDescriptor(@Nonnull Builder builder) {
        this.moduleClass = builder.moduleClass;

        this.identifier = builder.identifier;
        this.dependencies = Collections.unmodifiableSet(builder.dependencies);
        this.system = builder.system;
    }

    // ===

    @Nonnull
    public Class<? extends Module> getModuleClass() {
        return this.moduleClass;
    }

    //

    @Nonnull
    public ModuleIdentifier getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public Set<ModuleIdentifier> getDependencies() {
        return this.dependencies;
    }

    //

    public boolean isSystemModule() {
        return system;
    }

    // ===

    @Nonnull
    Module create(@Nonnull ModuleManager manager) {

        Constructor<? extends Module> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.moduleClass.getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            LOG.atSevere().withCause(e).log("Required constructor not found!");
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            LOG.atSevere().withCause(e).log("Access to constructor failed!");
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        Module module;

        try {
            module = constructor.newInstance();
        } catch (Exception e) {
            LOG.atSevere().withCause(e).log("Failed to create new Module instance!");
            throw new IllegalStateException("Failed to create new Module instance!", e);
        }

        // initialize the module
        module.init(manager, this.identifier);

        //

        return module;
    }

    // ===

    /**
     * Used to collect the module properties while parsing
     */
    private static class Builder {

        private Class<? extends Module> moduleClass;

        //

        private ModuleIdentifier identifier;
        private Set<ModuleIdentifier> dependencies;
        private boolean system;

        // ======

        Builder setModuleClass(@Nonnull Class<? extends Module> moduleClass) {
            this.moduleClass = moduleClass;
            return this;
        }

        Builder setIdentifier(@Nonnull ModuleIdentifier identifier) {
            this.identifier = identifier;
            return this;
        }

        Builder setDependencies(@Nonnull Set<ModuleIdentifier> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        @Nonnull
        public Builder setSystem(boolean system) {
            this.system = system;
            return this;
        }
    }

    // ===

    @Nonnull
    static ModuleDescriptor parse(@Nonnull Class<? extends Module> moduleClass) throws ModuleClassException {

        Builder builder = new Builder();

        builder.setModuleClass(moduleClass);

        // ======

        // ensure class and constructor properties

        if(!Modifier.isFinal(moduleClass.getModifiers())) throw new ModuleClassException("Module class is not final: " + moduleClass);

        if(moduleClass.getDeclaredConstructors().length != 1)
            throw new ModuleClassException("Module class requires exactly one constructor: " + moduleClass);

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends Module> constructor = moduleClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new ModuleClassException("Module class constructor is not private: " + moduleClass);

        } catch (NoSuchMethodException e) {
            throw new ModuleClassException("Module class does not contain a no-argument constructor: " + moduleClass, e);
        }

        // ======

        // find annotations
        MIdentifier annIdentifier = moduleClass.getDeclaredAnnotation(MIdentifier.class);
        Set<MRequires> annsRequires = Annotations.collectOf(moduleClass, MRequires.class);
        MSystem annSystem = moduleClass.getDeclaredAnnotation(MSystem.class);

        // ensure required annotations
        if(annIdentifier == null) throw new ModuleClassException("Module class is missing @MIdentifier annotation: " + moduleClass);

        // ======

        // generate identifier
        ModuleIdentifier identifier;

        try {
            identifier = ModuleIdentifier.parse(annIdentifier.value());
        } catch (IllegalArgumentException ex) {
            throw new ModuleClassException("Could not generate identifier for module class: " + moduleClass, ex);
        }

        builder.setIdentifier(identifier);

        // ======

        // collect dependencies

        Set<ModuleIdentifier> dependencies = new HashSet<>();

        for (MRequires require : annsRequires) {
            try {
                ModuleIdentifier dependencyIdentifier = ModuleIdentifier.parse(require.value());
                dependencies.add(dependencyIdentifier);
            } catch (IllegalArgumentException ex) {
                throw new ModuleClassException("Could not generate identifier for a module dependency!", ex);
            }
        }

        builder.setDependencies(dependencies);

        // ======

        builder.setSystem(annSystem != null);

        // ======

        return new ModuleDescriptor(builder);

    }
}
