/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.localization;

import cynoodle.core.api.measurements.MassUnit;
import cynoodle.core.api.measurements.TemperatureUnit;
import cynoodle.core.discord.UEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Optional;

import static cynoodle.core.mongo.fluent.FluentValues.*;

/**
 * Localization preferences for a User.
 */
@EIdentifier("base:localization:localization")
public final class Localization extends UEntity {
    private Localization() {}

    public static final ZoneId DEF_TIMEZONE = ZoneId.of("Z"); // UTC
    public static final Currency DEF_CURRENCY = Currency.getInstance("EUR");
    public static final TemperatureUnit DEF_TEMPERATURE_UNIT = TemperatureUnit.CELSIUS;
    public static final MassUnit DEF_MASS_UNIT = MassUnit.KILOGRAM;

    // ===

    /**
     * The timezone.
     */
    private ZoneId timezone = DEF_TIMEZONE;

    /**
     * The currency.
     */
    private Currency currency = DEF_CURRENCY;

    /**
     * The temperature unit.
     */
    private TemperatureUnit temperatureUnit = DEF_TEMPERATURE_UNIT;

    /**
     * The mass unit.
     */
    private MassUnit massUnit = DEF_MASS_UNIT;

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
