/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.discord;

import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Utility for everything about {@link net.dv8tion.jda.api.entities.Role Roles}.
 */
public final class Roles {
    private Roles() {}

    // === PARSING ===

    @Nonnull
    public static RParser parserAt(@Nonnull DiscordPointer guild) {
        return new RParser(guild);
    }

    @Nonnull
    public static RParser parserAt(@Nonnull Guild guild) {
        return new RParser(DiscordPointer.to(guild));
    }
}
