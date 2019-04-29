/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
