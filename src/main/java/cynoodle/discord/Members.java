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

import cynoodle.base.commands.CommandContext;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Utility for everything about {@link net.dv8tion.jda.api.entities.Member Members}.
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
