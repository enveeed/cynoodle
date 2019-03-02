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
public final class IntegerParser {

    private final static IntegerParser instance = new IntegerParser();

    // ===

    public int parse(@Nonnull String input) throws ParsingException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParsingException("Illegal integer number (32-bit number): `" + input + "`", e);
        }
    }

    // ===

    @Nonnull
    public static IntegerParser get() {
        return instance;
    }
}
