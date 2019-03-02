/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.localization;

import cynoodle.core.api.measurements.MassUnit;
import cynoodle.core.api.measurements.TemperatureUnit;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.Currency;

/**
 * Context for localization.
 */
public final class LocalizationContext {

    private final ZoneId timezone;
    private final Currency currency;
    private final TemperatureUnit temperatureUnit;
    private final MassUnit massUnit;

    // ===

    private LocalizationContext(
            @Nonnull ZoneId timezone,
            @Nonnull Currency currency,
            @Nonnull TemperatureUnit temperatureUnit,
            @Nonnull MassUnit massUnit) {

        this.timezone = timezone;
        this.currency = currency;
        this.temperatureUnit = temperatureUnit;
        this.massUnit = massUnit;

    }

    // ===

    @Nonnull
    public ZoneId getTimezone() {
        return this.timezone;
    }

    @Nonnull
    public Currency getCurrency() {
        return this.currency;
    }

    @Nonnull
    public TemperatureUnit getTemperatureUnit() {
        return this.temperatureUnit;
    }

    @Nonnull
    public MassUnit getMassUnit() {
        return this.massUnit;
    }


    // ===

    @Nonnull
    public static LocalizationContext of(@Nonnull Localization localization) {
        return new LocalizationContext(
                localization.getTimezone().orElse(Localization.DEF_TIMEZONE),
                localization.getCurrency().orElse(Localization.DEF_CURRENCY),
                localization.getTemperatureUnit().orElse(Localization.DEF_TEMPERATURE_UNIT),
                localization.getMassUnit().orElse(Localization.DEF_MASS_UNIT)
        );
    }

    @Nonnull
    public static LocalizationContext ofDefault() {
        return new LocalizationContext(
                Localization.DEF_TIMEZONE,
                Localization.DEF_CURRENCY,
                Localization.DEF_TEMPERATURE_UNIT,
                Localization.DEF_MASS_UNIT
        );
    }

}
