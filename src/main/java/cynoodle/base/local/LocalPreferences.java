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

import cynoodle.api.measurements.MassUnit;
import cynoodle.api.measurements.TemperatureUnit;
import cynoodle.discord.UEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Optional;

import static cynoodle.mongo.fluent.FluentValues.*;

/**
 * Localization preferences for a User.
 */
@EIdentifier("base:local:preferences")
public final class LocalPreferences extends UEntity {
    private LocalPreferences() {}

    static final ZoneId              DEF_TIMEZONE            = ZoneId.of("UTC");
    static final Currency            DEF_CURRENCY            = Currency.getInstance("EUR");
    static final TemperatureUnit     DEF_TEMPERATURE_UNIT    = TemperatureUnit.CELSIUS;
    static final MassUnit            DEF_MASS_UNIT           = MassUnit.KILOGRAM;

    // ===

    /**
     * The timezone.
     */
    private ZoneId timezone = null;

    /**
     * The currency.
     */
    private Currency currency = null;

    /**
     * The temperature unit.
     */
    private TemperatureUnit temperatureUnit = null;

    /**
     * The mass unit.
     */
    private MassUnit massUnit = null;

    // ===

    @Nonnull
    public Optional<ZoneId> getTimezone() {
        return Optional.ofNullable(this.timezone);
    }

    public void setTimezone(@Nullable ZoneId timezone) {
        this.timezone = timezone;
    }

    @Nonnull
    public Optional<Currency> getCurrency() {
        return Optional.ofNullable(this.currency);
    }

    public void setCurrency(@Nullable Currency currency) {
        this.currency = currency;
    }

    @Nonnull
    public Optional<TemperatureUnit> getTemperatureUnit() {
        return Optional.ofNullable(temperatureUnit);
    }

    public void setTemperatureUnit(@Nullable TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    @Nonnull
    public Optional<MassUnit> getMassUnit() {
        return Optional.ofNullable(this.massUnit);
    }

    public void setMassUnit(@Nullable MassUnit massUnit) {
        this.massUnit = massUnit;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.timezone = source.getAt("timezone").asNullable(toZoneId()).or(this.timezone);
        this.currency = source.getAt("currency").asNullable(toCurrency()).or(this.currency);
        this.temperatureUnit = source.getAt("unit_temperature").asNullable(toTemperatureUnit()).or(this.temperatureUnit);
        this.massUnit = source.getAt("unit_mass").asNullable(toMassUnit()).or(this.massUnit);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("timezone").asNullable(fromZoneId()).to(this.timezone);
        data.setAt("currency").asNullable(fromCurrency()).to(this.currency);
        data.setAt("unit_temperature").asNullable(fromTemperatureUnit()).to(this.temperatureUnit);
        data.setAt("unit_mass").asNullable(fromMassUnit()).to(this.massUnit);

        return data;
    }
}
