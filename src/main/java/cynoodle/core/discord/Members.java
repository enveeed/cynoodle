/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.base.commands.CommandContext;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Utility for everything about {@link net.dv8tion.jda.core.entities.Member Members}.
 */
public final class Members {
    private Members() {}

    // === FORMATTING ===

    @Nonnull
    public static MFormatter formatAt(@Nonnull DiscordPointer guild) {
        return new MFormatter(guild);
    }

    @Nonnull
    public static MFormatter formatAt(@Nonnull Guild guild) {
        return new MFormatter(DiscordPointer.to(guild));
    }

    @Nonnull
    public static MFormatter formatOf(@Nonnull CommandContext context) {
        return new MFormatter(context.getGuildPointer());
    }

    // === PARSING ===

    @Nonnull
    public static MParser parserAt(@Nonnull DiscordPointer guild) {
        return new MParser(guild);
    }

    @Nonnull
    public static MParser parserAt(@Nonnull Guild guild) {
        return new MParser(DiscordPointer.to(guild));
    }

    @Nonnull
    public static MParser parserOf(@Nonnull CommandContext context) {
        return new MParser(context.getGuildPointer())
                .withSelfUser(context.getUserPointer());
    }
}
