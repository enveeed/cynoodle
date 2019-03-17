/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.local;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.measurements.MassUnit;
import cynoodle.core.api.measurements.TemperatureUnit;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Currency;
import java.util.Locale;

/**
 * Context for localization.
 */
public final class LocalContext {

    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ===

    private final ZoneId timezone;
    private final Currency currency;
    private final TemperatureUnit temperatureUnit;
    private final MassUnit massUnit;

    // ===

    private LocalContext(
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
    public String formatTime(@Nonnull TemporalAccessor time) {
        ZoneId val = getTimezone();
        String out = FORMATTER_TIME.withZone(val)
                .format(time);
        out = out + " " + val.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return out;
    }

    @Nonnull
    public String formatDate(@Nonnull TemporalAccessor time) {
        ZoneId val = getTimezone();
        return FORMATTER_DATE.withZone(val)
                .format(time);
        // timezone is not really needed for a date only but maybe make this settable
    }

    @Nonnull
    public String formatDateTime(@Nonnull TemporalAccessor time) {
        ZoneId val = getTimezone();
        String out = FORMATTER_DATETIME.withZone(val)
                .format(time);
        out = out + " " + val.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return out;
    }

    @Nonnull
    public String formatCurrency(double value) {
        return getCurrency().getCurrencyCode() + " " + Numbers.format(value, 2);
    }

    @Nonnull
    public String formatTemperature(double value) {
        return getTemperatureUnit()
                .format(value);
    }

    @Nonnull
    public String formatMass(double value) {
        return getMassUnit()
                .format(value);
    }

    // ===

    /**
     * Create a localization context using the given localization preferences.
     * If the preferences do not contain a value for a parameter, the default will be used instead.
     * @param localization the localization preferences
     * @return the localization context
     */
    @Nonnull
    public static LocalContext of(@Nonnull LocalPreferences localization) {
        return new LocalContext(
                localization.getTimezone().orElse(LocalPreferences.DEF_TIMEZONE),
                localization.getCurrency().orElse(LocalPreferences.DEF_CURRENCY),
                localization.getTemperatureUnit().orElse(LocalPreferences.DEF_TEMPERATURE_UNIT),
                localization.getMassUnit().orElse(LocalPreferences.DEF_MASS_UNIT)
        );
    }

    /**
     * Create a localization context using only default values.
     * @return the localization context
     */
    @Nonnull
    public static LocalContext ofDefault() {
        return new LocalContext(
                LocalPreferences.DEF_TIMEZONE,
                LocalPreferences.DEF_CURRENCY,
                LocalPreferences.DEF_TEMPERATURE_UNIT,
                LocalPreferences.DEF_MASS_UNIT
        );
    }

}
