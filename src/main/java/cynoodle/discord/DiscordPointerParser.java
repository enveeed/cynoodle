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

import cynoodle.util.parsing.ParsingException;
import cynoodle.util.parsing.PrimitiveParsers;

import javax.annotation.Nonnull;

/**
 * Parser for {@link DiscordPointer DiscordPointers} (Discord snowflake IDs).
 */
public final class DiscordPointerParser {

    private final static DiscordPointerParser instance = new DiscordPointerParser();

    // ===

    @Nonnull
    public DiscordPointer parse(@Nonnull String input) throws ParsingException {

        long id;

        try {
            id = PrimitiveParsers.parseLong().parse(input);
            if(id <= 0) throw new ParsingException("Discord snowflake IDs cannot be less or equal than zero!");
        } catch (ParsingException e) {
            throw new ParsingException("Invalid snowflake ID: " + e.getMessage(), e);
        }

        return DiscordPointer.to(id);

    }

    // ===

    @Nonnull
    public static DiscordPointerParser get() {
        return instance;
    }
}
