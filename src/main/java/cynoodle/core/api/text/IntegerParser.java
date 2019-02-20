/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for integers.
 */
public final class IntegerParser implements Parser<Integer> {

    private final static IntegerParser instance = new IntegerParser();

    // ===

    @Override
    public Integer parse(@Nonnull String input) throws ParserException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParserException("Illegal integer number (32-bit number): `" + input + "`", e);
        }
    }

    // ===

    @Nonnull
    public static IntegerParser get() {
        return instance;
    }
}
