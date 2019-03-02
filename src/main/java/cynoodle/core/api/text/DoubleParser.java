/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for doubles.
 */
public final class DoubleParser {

    private final static DoubleParser instance = new DoubleParser();

    // ===

    public double parse(@Nonnull String input) throws ParsingException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ParsingException("Illegal decimal number: `" + input + "`", e);
        }
    }

    // ===

    @Nonnull
    public static DoubleParser get() {
        return instance;
    }
}
