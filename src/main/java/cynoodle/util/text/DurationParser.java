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

package cynoodle.util.text;

import cynoodle.util.parsing.Parser;
import cynoodle.util.parsing.ParsingException;
import cynoodle.util.parsing.PrimitiveParsers;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * Parser for {@link Duration Durations}.
 */
public final class DurationParser implements Parser<Duration> {

    private final static DurationParser instance = new DurationParser();

    // ===

    @Nonnull
    public Duration parse(@Nonnull String input) throws ParsingException {
        // TODO replace this with actual duration parsing as right now its only parsing days
        return Duration.ofDays(PrimitiveParsers.parseLong().parse(input));
    }

    // ===

    @Nonnull
    public static DurationParser get() {
        return instance;
    }
}
