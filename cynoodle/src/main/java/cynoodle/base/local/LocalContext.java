/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.local;

import cynoodle.util.Numbers;
import cynoodle.util.measurements.MassUnit;
import cynoodle.util.measurements.TemperatureUnit;

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
