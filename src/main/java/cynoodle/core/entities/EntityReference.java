/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import javax.annotation.Nonnull;
import java.util.Optional;

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
}
