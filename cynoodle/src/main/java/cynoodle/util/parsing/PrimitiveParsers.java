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

package cynoodle.util.parsing;

import javax.annotation.Nonnull;

/**
 * Static utility for parsing of primitive types.
 */
public final class PrimitiveParsers {
    private PrimitiveParsers() {}

    // ===

    /**
     * Case-insensitive values for boolean <code>true</code>.
     */
    private static final String[] VALUES_TRUE
            = new String[]{"true","1","yes","y","on","enable","enabled", "allow"};
    /**
     * Case-insensitive values for boolean <code>false</code>.
     */
    private static final String[] VALUES_FALSE
            = new String[]{"false","0","no","n","off","disable","disabled", "deny"};

    // ===

    /**
     * Return a parser for {@link Boolean Booleans}.
     * @return a boolean parser
     */
    @Nonnull
    public static Parser<Boolean> parseBoolean() {
        return input -> {
            for (String val : VALUES_TRUE) if(input.equalsIgnoreCase(val)) return true;
            for (String val : VALUES_FALSE) if(input.equalsIgnoreCase(val)) return false;
            throw new ParsingException("Invalid boolean value: `" + input + "`.");
        };
    }

    /**
     * Return a parser for {@link Double Doubles}.
     * @return a double parser
     */
    @Nonnull
    public static Parser<Double> parseDouble() {
        return input -> {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                throw new ParsingException("Invalid decimal number: `" + input + "`", e);
            }
        };
    }

    /**
     * Return a parser for {@link Integer Integers}.
     * @return an int parser
     */
    @Nonnull
    public static Parser<Integer> parseInteger() {
        return input -> {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                throw new ParsingException("Invalid integer number (32-bit number): `" + input + "`", e);
            }
        };
    }

    /**
     * Return a parser for {@link Long Longs}.
     * @return a long parser
     */
    @Nonnull
    public static Parser<Long> parseLong() {
        return input -> {
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                throw new ParsingException("Invalid integer number (64-bit number): `" + input + "`", e);
            }
        };
    }

}
