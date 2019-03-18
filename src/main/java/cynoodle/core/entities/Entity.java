/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import cynoodle.core.api.Snowflake;
import cynoodle.core.mongo.IBson;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * An Entity, a persistent (on MongoDB), uniquely identified (via snowflakes) and cached data object.
 */
public abstract class Entity implements IBson {

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
     * @see Snowflake
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
    public EntityManager<?> getManager() {
        return this.manager;
    }

    /**
     * Get the {@link EntityManager} for this Entity, casted to
     * a useful type rather than using the wildcard. Given type must match
     * the actual entity class.
     * @param entityClass the entity class of this entity
     * @param <E> the entity type
     * @return the casted manager
     */
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

    // ===

    @Nonnull
    public final EntityReference<?> reference() {
        return getManager().reference(this.id);
    }

    @Nonnull
    public final <E extends Entity> EntityReference<E> reference(@Nonnull Class<E> entityClass) {
        return getManager(entityClass).reference(this.id);
    }

    // ===

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (id != entity.id) return false;
        return manager.equals(entity.manager);
    }

    @Override
    public int hashCode() {
        int result = manager.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}