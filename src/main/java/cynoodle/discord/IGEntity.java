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

import cynoodle.entities.IEntity;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * superinterface for public interfaces to {@link GEntity}.
 */
public interface IGEntity extends IEntity {

    /**
     * Get an optional containing a pointer to the Guild,
     * if one is set, otherwise empty
     * @return guild pointer optional
     */
    @Nonnull
    Optional<DiscordPointer> getGuild();

    /**
     * Attempt to get a pointer to the Guild, throws if empty.
     * @return guild pointer
     * @throws IllegalStateException if there is no guild set
     */
    @Nonnull
    default DiscordPointer requireGuild() throws IllegalStateException {
        return getGuild().orElseThrow(() -> new IllegalStateException("No Guild set."));
    }

    /**
     * Attempt to get the ID of the Guild, throws if empty.
     * @return guild ID
     * @throws IllegalStateException if there is no guild set
     */
    default long getGuildID() throws IllegalStateException {
        return requireGuild().getID();
    }

    //

    /**
     * Set the guild to the given pointer.
     * @param guild the guild
     */
    void setGuild(@Nullable DiscordPointer guild);

    /**
     * Set the guild to the given Guild.
     * @param guild the guild
     */
    default void setGuild(@Nullable Guild guild) {
        setGuild(guild == null ? null : DiscordPointer.to(guild));
    }

    //

    /**
     * Check if a guild is set.
     * @return true if there is a guild, false otherwise.
     */
    default boolean hasGuild() {
        return getGuild().isPresent();
    }

}
