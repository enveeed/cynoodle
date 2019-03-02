/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Static utility for parsing functions for primitive types.
 */
public final class PrimitiveParsers {
    private PrimitiveParsers() {}

    // ===

    @Nonnull
    public static Function<String, Boolean> parseBoolean() {
        return BooleanParser.get()::parse;
    }

    @Nonnull
    public static Function<String, Double> parseDouble() {
        return DoubleParser.get()::parse;
    }

    @Nonnull
    public static Function<String, Integer> parseInteger() {
        return IntegerParser.get()::parse;
    }

    @Nonnull
    public static Function<String, Long> parseLong() {
        return LongParser.get()::parse;
    }

}
