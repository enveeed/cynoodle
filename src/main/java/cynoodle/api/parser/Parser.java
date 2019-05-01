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

package cynoodle.api.parser;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A parser is an object which can transform {@link String Strings}
 * in a specific expected format into an object of type {@link T}.
 * @param <T> the result type
 */
public interface Parser<T> {

    /**
     * Parse the given input into a result object.
     * @param input the input string
     * @return the result object
     * @throws ParsingException if parsing of the input failed, e.g. if the format
     * was not of the expected form or the input does not fit into the range of the expected type.
     */
    @Nonnull
    T parse(@Nonnull String input) throws ParsingException;

    // ===

    /**
     * Return a {@link Function} of this parser which calls {@link #parse(String)}
     * for its input.
     * @return a parser function
     */
    @Nonnull
    default Function<String, T> asFunction() {
        return this::parse;
    }

}
