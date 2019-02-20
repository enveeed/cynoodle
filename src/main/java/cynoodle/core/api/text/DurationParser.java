/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * Parser for {@link Duration Durations}.
 */
public final class DurationParser implements Parser<Duration> {

    private final static DurationParser instance = new DurationParser();

    // ===

    @Override
    public Duration parse(@Nonnull String input) throws ParserException {
        // TODO replace this with actual duration parsing as right now its only parsing days
        return Duration.ofDays(LongParser.get().parse(input));
    }

    // ===

    @Nonnull
    public static DurationParser get() {
        return instance;
    }
}
