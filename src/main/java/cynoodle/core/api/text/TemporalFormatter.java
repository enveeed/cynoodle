/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import cynoodle.core.base.local.LocalPreferences;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Formatter for any {@link TemporalAccessor TemporalAccessors}.
 */
public final class TemporalFormatter {

    public static final DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final ZoneId DEF_TIMEZONE = ZoneOffset.UTC;

    // ===

    private final DateTimeFormatter formatter;

    // ===

    private TemporalFormatter(@Nonnull DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    // ===

    @Nonnull
    public String format(@Nonnull TemporalAccessor input) {
        return this.formatter.format(input);
    }

    // ===

    public void apply(@Nonnull LocalPreferences localization) {
        this.formatter.withZone(localization.getTimezone().orElse(DEF_TIMEZONE));
    }

    // ===

    @Nonnull
    public static TemporalFormatter of(@Nonnull DateTimeFormatter formatter) {
        return new TemporalFormatter(formatter);
    }

    @Nonnull
    public static TemporalFormatter ofDefault() {
        return new TemporalFormatter(DEFAULT);
    }
}
