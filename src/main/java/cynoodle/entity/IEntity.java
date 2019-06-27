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

package cynoodle.entity;

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
     * @see cynoodle.util.Snowflake
     * @see CyNoodle#CYNOODLE_EPOCH
     */
    @Nonnull
    Instant getCreationTime();

    //

    @Nonnull
    String getIDString();

    @Nonnull
    String getIDStringBase62();

    // ===

    void persist() throws IllegalStateException, EntityIOException;

    void update() throws IllegalStateException, NoSuchElementException, EntityIOException;
}
