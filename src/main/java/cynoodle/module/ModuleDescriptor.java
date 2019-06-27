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
import cynoodle.BuildConfig;
import cynoodle.module.annotations.Identifier;
import cynoodle.module.annotations.Requires;
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

    /**
     * The identifier (ID) of the Module.
     */
    private final ModuleIdentifier identifier;
    /**
     * The version of the Module.
     */
    private final String version;

    /**
     * The class of the Module.
     */
    private final Class<? extends Module> moduleClass;

    /**
     * The dependencies of the Module.
     */
    private final Set<ModuleIdentifier> dependencies;

    /**
     * The meta-data of the Module.
     */
    private final ModuleMeta meta;

    /**
     * If this Module is internal.
     */
    private final boolean internal;

    // ===

    private ModuleDescriptor(@Nonnull ModuleIdentifier identifier, @Nonnull String version,
                             @Nonnull Class<? extends Module> moduleClass,
                             @Nonnull Collection<ModuleIdentifier> dependencies,
                             @Nonnull ModuleMeta meta,
                             boolean internal) {
        this.identifier = identifier;
        this.version = version;

        this.moduleClass = moduleClass;

        this.dependencies = Set.copyOf(dependencies);
        this.meta = meta;

        this.internal = internal;
    }

    // ===

    @Nonnull
    public ModuleIdentifier getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public String getVersion() {
        return this.version;
    }

    //

    @Nonnull
    public Class<? extends Module> getModuleClass() {
        return this.moduleClass;
    }

    //

    @Nonnull
    public Set<ModuleIdentifier> getDependencies() {
        return this.dependencies;
    }

    //

    @Nonnull
    public ModuleMeta getMeta() {
        return this.meta;
    }

    //

    public boolean isInternal() {
        return this.internal;
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

    @Nonnull
    private static Class<? extends Module> verifyClass(@Nonnull Class<?> cl) throws ModuleClassException {

        // ensure class is actually a Module class
        Class<? extends Module> moduleClass;
        try {
            moduleClass = cl.asSubclass(Module.class);
        } catch (ClassCastException e) {
            throw new ModuleClassException("Module class must extend Module!", e);
        }

        // ensure class and constructor properties

        if(!Modifier.isFinal(moduleClass.getModifiers()))
            throw new ModuleClassException("Module class is not final: " + moduleClass);

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

        return moduleClass;
    }

    // ===

    @Nonnull
    public static ModuleDescriptor ofFile(@Nonnull ModuleDescriptorFile file)
            throws ModuleClassException {

        Class<?> definedClass;

        try {
            definedClass = Class.forName(file.getClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class " + file.getClassName() + " could not be found!", e);
        }

        Class<? extends Module> moduleClass = verifyClass(definedClass);

        //

        return new ModuleDescriptor(file.getIdentifier(), file.getVersion(),
                moduleClass,
                file.getDependencies(),
                file.getMeta(),
                false);
    }

    @Nonnull
    public static ModuleDescriptor ofInternal(@Nonnull Class<? extends Module> moduleClass, @Nonnull ModuleMeta meta)
            throws ModuleClassException {

        moduleClass = verifyClass(moduleClass);

        // find annotations
        Identifier annIdentifier = moduleClass.getDeclaredAnnotation(Identifier.class);
        Set<Requires> annsRequires = Annotations.collectOf(moduleClass, Requires.class);

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

        // ======

        // collect dependencies

        Set<ModuleIdentifier> dependencies = new HashSet<>();

        for (Requires require : annsRequires) {
            try {
                ModuleIdentifier dependencyIdentifier = ModuleIdentifier.parse(require.value());
                dependencies.add(dependencyIdentifier);
            } catch (IllegalArgumentException ex) {
                throw new ModuleClassException("Could not generate identifier for a module dependency!", ex);
            }
        }

        // ===

        // TODO maybe not use the cynoodle version for this and not always make it internal
        return new ModuleDescriptor(identifier, BuildConfig.VERSION,
                moduleClass,
                dependencies,
                meta,
                true);
    }
}
