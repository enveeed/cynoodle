/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import net.dv8tion.jda.core.entities.ISnowflake;

import javax.annotation.Nonnull;

/**
 * Immutable pointer to a uniquely snowflake-identified entity in Discord.
 */
public final class DiscordPointer {

    private final long id;

    //

    private DiscordPointer(long id) {
        this.id = id;
    }

    //

    public long getID() {
        return this.id;
    }

    //

    @Nonnull
    public static DiscordPointer to(long snowflake) {
        if(snowflake <= 0L) throw new IllegalArgumentException("Illegal snowflake: "+snowflake);
        return new DiscordPointer(snowflake);
    }

    @Nonnull
    public static DiscordPointer to(@Nonnull ISnowflake entity) {
        return new DiscordPointer(entity.getIdLong());
    }
}
