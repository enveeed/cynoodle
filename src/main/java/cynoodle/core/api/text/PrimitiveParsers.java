/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

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
            = new String[]{"true","1","yes","y","on","enable","enabled"};
    /**
     * Case-insensitive values for boolean <code>false</code>.
     */
    private static final String[] VALUES_FALSE
            = new String[]{"false","0","no","n","off","disable","disabled"};

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
