/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.entities;

import cynoodle.CyNoodle;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * superinterface for public interfaces to {@link Entity Entities},
 * to prevent exposing implementation details.
 */
public interface IEntity {

    /**
     * Get the snowflake ID of this Entity.
     * @return the ID
     */
    long getID();

    /**
     * Get the creation time of this Entity.
     * @return an Instant of the creation time of this Entity
     * @see cynoodle.api.Snowflake
     * @see CyNoodle#CYNOODLE_EPOCH
     */
    @Nonnull
    Instant getCreationTime();

    // ===

    void persist() throws IllegalStateException, EntityIOException;

    void update() throws IllegalStateException, NoSuchElementException, EntityIOException;
}
