/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import cynoodle.core.mongo.BsonData;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * An Entity, a persistent (on MongoDB), uniquely identified (via snowflakes) and cached data object.
 */
public abstract class Entity implements BsonData {

    private EntityManager<?> manager;
    private long id;

    //

    /**
     * Internal initialization method for entity instances.
     * @param manager the entity manager
     * @param id the entity snowflake ID
     */
    final void init(@Nonnull EntityManager<?> manager, long id) {
        this.manager = manager;
        this.id = id;
    }

    // === ID ===

    /**
     * Get the snowflake ID of this Entity.
     * @return the ID
     */
    public final long getID() {
        return this.id;
    }

    /**
     * Get the creation time of this Entity.
     * @return an Instant of the creation time of this Entity
     * @see cynoodle.core.Snowflake
     * @see cynoodle.core.CyNoodle#CYNOODLE_EPOCH
     */
    @Nonnull
    public final Instant getCreationTime() {
        return this.manager.getSnowflake()
                .getCreationTime(this.id);
    }

    // === MANAGER ===

    /**
     * Get the {@link EntityManager} for this Entity.
     * @return the manager
     */
    @Nonnull
    public final EntityManager<?> getManager() {
        return this.manager;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public final <E extends Entity> EntityManager<E> getManager(@Nonnull Class<E> entityClass) {
        if(entityClass == this.getType().getDescriptor().getEntityClass())
            return (EntityManager<E>) this.manager;
        else throw new IllegalArgumentException("Class does not equal the actual Entity class!");
    }

    // == TYPE ==

    /**
     * Get the {@link EntityType} of this Entity.
     * @return the EntityType
     */
    @Nonnull
    public final EntityType<?> getType() {
        return getManager().getType();
    }

    @Nonnull
    public final String getTypeIdentifier() {
        return getType().getIdentifier();
    }

    //

    @Override
    public String toString() {
        return getTypeIdentifier()+"-"+getID();
    }

    // === DATA ==

    public void persist() throws IllegalStateException, EntityIOException {
        this.manager.persist(this.id);
    }

    public void update() throws IllegalStateException, NoSuchElementException, EntityIOException {
        this.manager.update(this.id);
    }

    // ===

    public void delete() throws NoSuchElementException, EntityIOException {
        this.manager.delete(this.id);
    }
}