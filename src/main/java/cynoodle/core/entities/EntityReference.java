/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

/**
 * Immutable reference to any Entity with a snowflake ID.
 * @param <E> the entity type
 */
public final class EntityReference<E extends Entity> {

    private final EntityManager<E> manager;
    private final long id;

    // ===

    EntityReference(@Nonnull EntityManager<E> manager, long id) {
        this.manager = manager;
        this.id = id;
    }

    // ===

    /**
     * Get the manager which manages the referenced Entity type.
     * @return the entity manager
     */
    @Nonnull
    public EntityManager<E> getManager() {
        return this.manager;
    }

    /**
     * Get the referenced snowflake ID.
     * @return the snowflake ID
     */
    public long getId() {
        return this.id;
    }

    // ===

    /**
     * Attempt to get the referenced Entity from the manager.
     * @return an optional containing the Entity if it exists, otherwise empty
     */
    @Nonnull
    public Optional<E> get() {
        return this.manager.get(id);
    }

    /**
     * Check if the referenced Entity exists.
     * @return true if the entity exists, false otherwise
     */
    public boolean exists() {
        return this.manager.exists(id);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityReference<?> that = (EntityReference<?>) o;

        if (id != that.id) return false;
        return manager.equals(that.manager);
    }

    @Override
    public int hashCode() {
        int result = manager.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    // ===

    @Nonnull
    public static <E extends Entity> Function<EntityReference<E>, BsonValue> store() {
        return ref -> new BsonInt64(ref.getId());
    }

    @Nonnull
    public static <E extends Entity> Function<BsonValue, EntityReference<E>> load(@Nonnull EntityManager<E> manager) {
        return value -> manager.reference(value.asInt64().getValue());
    }
}
