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

package cynoodle.discord;

import cynoodle.util.Checks;
import net.dv8tion.jda.api.utils.TimeUtil;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Common superclass for all references to Discord entities
 * which are identified with a snowflake ID.
 */
public abstract class DiscordReference {

    /**
     * The Discord snowflake ID.
     */
    private final long id;

    // ===

    DiscordReference(long id) {
        Checks.notNegative(id);
        this.id = id;
    }

    // ===

    /**
     * Get the snowflake ID this reference points to.
     * @return the snowflake ID
     */
    public long getID() {
        return this.id;
    }

    /**
     * Get the creation time instant the snowflake ID of this reference contains
     * @return the creation time of the snowflake ID
     */
    @Nonnull
    public Instant getCreationTime() {
        return TimeUtil.getTimeCreated(this.id).toInstant();
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscordReference that = (DiscordReference) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
