/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * Type information for a {@link Embed} class.
 */
public final class EmbedType<E extends Embed> {

    private final Class<E> embedClass;
    private final String identifier;

    // ===

    private EmbedType(@Nonnull Class<E> embedClass, @Nonnull String identifier) {
        this.embedClass = embedClass;
        this.identifier = identifier;
    }

    // ===

    @Nonnull
    public Class<E> getEmbedClass() {
        return this.embedClass;
    }

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    // ===

    @Nonnull
    public E create(@Nonnull Consumer<E> action) {

        E instance = this.createInstance();

        action.accept(instance);

        return instance;
    }

    @Nonnull
    public E create() {
        return create(e -> {});
    }

    // === INTERNAL ===

    @SuppressWarnings("unchecked")
    @Nonnull
    private E createInstance() {

        Constructor<? extends Embed> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.getEmbedClass().getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        Embed embed;

        try {
            embed = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create new Embed instance!", e);
        }

        // initialize the embed
        embed.init(this);

        //

        return (E) embed;

    }

    //

    @Nonnull
    public static <E extends Embed> EmbedType<E> of(@Nonnull Class<E> embedClass, @Nonnull String identifier) throws EmbedClassException {

        // ensure class and constructor properties

        if(!Modifier.isFinal(embedClass.getModifiers())) throw new EmbedClassException("Embed class is not final: " + embedClass);

        if(embedClass.getDeclaredConstructors().length != 1)
            throw new EmbedClassException("Embed class requires exactly one constructor: " + embedClass);

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends Embed> constructor = embedClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new EmbedClassException("Embed class constructor is not private: " + embedClass);

        } catch (NoSuchMethodException e) {
            throw new EmbedClassException("Embed class does not contain a no-argument constructor: " + embedClass, e);
        }

        return new EmbedType<>(embedClass, identifier);
    }

}
