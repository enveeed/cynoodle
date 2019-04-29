/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.concurrent;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * Static factory methods to create different kinds of {@link Schedule Schedules}.
 */
public final class Schedules {
    private Schedules() {}

    // ===

    /**
     * Create a schedule that will delay execution for the
     * exact given amount of time when the schedule is queried.
     * @param amount the amount
     * @param unit the unit
     * @return the schedule
     */
    @Nonnull
    public static Schedule delay(long amount, @Nonnull TemporalUnit unit) {
        return () -> {
            Instant now = Instant.now();
            return now.plus(amount, unit);
        };
    }
}
