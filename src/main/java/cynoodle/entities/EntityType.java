/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.entities;

import com.google.common.flogger.FluentLogger;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.util.regex.Pattern;

/**
 * Type information for an {@link Entity} class.
 * @see EntityTypeDescriptor
 * @see Entity
 */
public final class EntityType<E extends Entity> {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ======

    /**
     * All entity type identifiers must match this pattern.
     */
    public static final Pattern IDENTIFIER_REGEX = Pattern.compile("\\w+");
    /**
     * All entity type identifiers must be less or equal than this in length.
     */
    public static final int IDENTIFIER_LIMIT = 120;

    // ======

    private final EntityTypeDescriptor descriptor;

    // ======

    private EntityType(@Nonnull EntityTypeDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    // ======

    @Nonnull
    public EntityTypeDescriptor getDescriptor() {
        return this.descriptor;
    }

    //

    @Nonnull
    public String getIdentifier() {
        return this.descriptor.getIdentifier();
    }

    @Nonnull
    public String getCollection() {
        return this.descriptor.getCollection();
    }

    // ======

    @Nonnull
    @SuppressWarnings("unchecked")
    E createInstance(@Nonnull EntityManager<?> manager, long id) {

        Constructor<? extends Entity> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.descriptor.getEntityClass().getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            LOG.atSevere().withCause(e).log("Required constructor not found!");
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            LOG.atSevere().withCause(e).log("Access to constructor failed!");
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        Entity entity;

        try {
            entity = constructor.newInstance();
        } catch (Exception e) {
            LOG.atSevere().withCause(e).log("Failed to create new Entity instance!");
            throw new IllegalStateException("Failed to create new Entity instance!", e);
        }

        // initialize the entity
        entity.init(manager, id);

        //

        return (E) entity;
    }

    // ======

    public static void validateIdentifier(@Nonnull String name) throws IllegalArgumentException {
        if(!IDENTIFIER_REGEX.matcher(name).find())
            throw new IllegalArgumentException("Entity type identifier is invalid: " + name);
        if(name.length() > IDENTIFIER_LIMIT)
            throw new IllegalArgumentException("Entity type identifier is too long (> 120 characters): "+name);
    }

    // ======

    @Nonnull
    public static <E extends Entity> EntityType<E> of(@Nonnull Class<E> entityClass) throws EntityClassException {
        EntityTypeDescriptor descriptor = EntityTypeDescriptor.parse(entityClass);
        return new EntityType<>(descriptor);
    }
}
