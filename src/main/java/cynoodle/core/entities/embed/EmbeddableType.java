/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * Type information for a {@link Embeddable} class.
 * Contains methods to create new instances.
 */
public final class EmbeddableType<E extends Embeddable> {

    private final Class<E> embeddableClass;
    private final String identifier;

    // ===

    private EmbeddableType(@Nonnull Class<E> embeddableClass, @Nonnull String identifier) {
        this.embeddableClass = embeddableClass;
        this.identifier = identifier;
    }

    // ===

    @Nonnull
    public Class<E> getEmbeddableClass() {
        return this.embeddableClass;
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

    @Nonnull
    public E createAndLoad(@Nonnull FluentDocument source) {
        return create(e -> e.fromBson(source));
    }

    // === INTERNAL ===

    @SuppressWarnings("unchecked")
    @Nonnull
    private E createInstance() {

        Constructor<? extends Embeddable> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.getEmbeddableClass().getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        Embeddable embed;

        try {
            embed = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create new Embed instance!", e);
        }

        // initialize the Embeddable
        embed.init(this);

        //

        return (E) embed;

    }

    //

    @Nonnull
    public static <E extends Embeddable> EmbeddableType<E> of(@Nonnull String identifier, @Nonnull Class<E> embedClass) throws EmbeddableClassException {

        // ensure class and constructor properties

        if(!Modifier.isFinal(embedClass.getModifiers())) throw new EmbeddableClassException("Embeddable class is not final: " + embedClass);

        if(embedClass.getDeclaredConstructors().length != 1)
            throw new EmbeddableClassException("Embeddable class requires exactly one constructor: " + embedClass);

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends Embeddable> constructor = embedClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new EmbeddableClassException("Embeddable class constructor is not private: " + embedClass);

        } catch (NoSuchMethodException e) {
            throw new EmbeddableClassException("Embeddable class does not contain a no-argument constructor: " + embedClass, e);
        }

        return new EmbeddableType<>(embedClass, identifier);
    }

}
