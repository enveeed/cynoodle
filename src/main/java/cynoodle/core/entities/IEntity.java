/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.NoSuchElementException;

public interface IEntity {

    /**
     * Get the snowflake ID of this Entity.
     * @return the ID
     */
    long getID();

    /**
     * Get the creation time of this Entity.
     * @return an Instant of the creation time of this Entity
     * @see cynoodle.core.api.Snowflake
     * @see cynoodle.core.CyNoodle#CYNOODLE_EPOCH
     */
    @Nonnull
    Instant getCreationTime();

    // === MANAGER ===

    /**
     * Get the {@link EntityManager} for this Entity.
     * @return the manager
     */
    @Nonnull
    EntityManager<?> getManager();

    /**
     * Get the {@link EntityManager} for this Entity, casted to
     * a useful type rather than using the wildcard. Given type must match
     * the actual entity class.
     * @param entityClass the class of this entity
     * @param <E> the entity type
     * @return the casted manager
     */
    @Nonnull
    <E extends Entity> EntityManager<E> getManager(@Nonnull Class<E> entityClass);

    // == TYPE ==

    /**
     * Get the {@link EntityType} of this Entity.
     * @return the EntityType
     */
    @Nonnull
    EntityType<?> getType();

    @Nonnull
    String getTypeIdentifier();

    // ===

    @Nonnull
    EntityReference<?> reference();

    @Nonnull
    <E extends Entity> EntityReference<E> reference(@Nonnull Class<E> entityClass);

    // === DATA ==

    void persist() throws IllegalStateException, EntityIOException;

    void update() throws IllegalStateException, NoSuchElementException, EntityIOException;

    // === INTERNAL ===

    private void ensureAssignableClass(@Nonnull Class<? extends IEntity> entityClass) throws IllegalArgumentException {

        Class<? extends Entity> expected = this.getType().getDescriptor().getEntityClass();

        if(!entityClass.isAssignableFrom(expected))
            throw new IllegalArgumentException("Actual Entity class is not assignable to given class:" +
                    " Got " + entityClass + " but expected a supertype of / directly " + expected + "!");
    }
}
