/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser which just directly returns the input String. Never fails.
 */
public final class StringParser implements Parser<String> {

    private static final StringParser instance = new StringParser();

    // ===

    @Override
    public String parse(@Nonnull String input) {
        return input;
    }

    // ===

    @Nonnull
    public static StringParser get() {
        return instance;
    }
}
