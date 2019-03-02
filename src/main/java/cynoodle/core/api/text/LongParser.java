/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for longs.
 */
public final class LongParser {

    private final static LongParser instance = new LongParser();

    // ===

    public long parse(@Nonnull String input) throws ParsingException {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new ParsingException("Illegal integer number (64-bit number): `" + input + "`", e);
        }
    }

    // ===

    @Nonnull
    public static LongParser get() {
        return instance;
    }
}
