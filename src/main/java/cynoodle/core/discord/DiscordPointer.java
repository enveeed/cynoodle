/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import net.dv8tion.jda.core.entities.ISnowflake;

import javax.annotation.Nonnull;

/**
 * Immutable pointer to any snowflake-identified entity within Discord.
 */
public final class DiscordPointer {

    private final long id;

    // ===

    private DiscordPointer(long id) {
        this.id = id;
    }

    // ===

    /**
     * Get the snowflake ID of the entity this pointer points to.
     * @return the pointer target ID
     */
    public long getID() {
        return this.id;
    }

    // ===

    /**
     * Create a new pointer to the given snowflake ID.
     * @param snowflake the snowflake ID
     * @return a new pointer pointing to the given ID.
     * @throws IllegalArgumentException if the snowflake ID was malformed
     */
    @Nonnull
    public static DiscordPointer to(long snowflake) throws IllegalArgumentException {
        if(snowflake <= 0L) throw new IllegalArgumentException("Illegal snowflake ID: "+snowflake);
        return new DiscordPointer(snowflake);
    }

    /**
     * Create a new pointer to the snowflake ID of the given JDA {@link ISnowflake}.
     * @param entity the JDA {@link ISnowflake} with the ID.
     * @return a new pointer pointing to the ID of the given JDA entity
     * @throws IllegalArgumentException if the snowflake ID of the given entity was malformed
     */
    @Nonnull
    public static DiscordPointer to(@Nonnull ISnowflake entity) {
        return to(entity.getIdLong());
    }
}
