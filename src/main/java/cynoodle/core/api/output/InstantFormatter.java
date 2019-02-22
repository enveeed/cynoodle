/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.output;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formatter for {@link Instant Instants}.
 */
public final class InstantFormatter implements Formatter<Instant> {

    private ZoneId zone = ZoneId.systemDefault();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ===

    private InstantFormatter() {}

    // ===

    @Nonnull
    @Override
    public String format(@Nonnull Instant input) {

        // create ZonedDateTime for formatting
        ZonedDateTime time = input.atZone(zone);

        return this.formatter.format(time);
    }

    // ===

    @Nonnull
    public InstantFormatter setZone(@Nonnull ZoneId zone) {
        this.zone = zone;
        return this;
    }

    @Nonnull
    public InstantFormatter setFormatter(@Nonnull DateTimeFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    // ===

    @Nonnull
    public static InstantFormatter create() {
        return new InstantFormatter();
    }
}
