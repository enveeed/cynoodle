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

package cynoodle.mongodb.fluent;

import cynoodle.util.measurements.MassUnit;
import cynoodle.util.measurements.TemperatureUnit;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.function.Function;

public final class FluentValues {
    private FluentValues() {}

    // time

    public static Function<Instant, BsonInt64> fromInstant() {
        return x -> new BsonInt64(x.toEpochMilli());
    }

    public static Function<BsonValue, Instant> toInstant() {
        return x -> Instant.ofEpochMilli(x.asInt64().getValue());
    }

    public static Function<LocalDate, BsonInt64> fromLocalDate() {
        return x -> new BsonInt64(x.toEpochDay());
    }

    public static Function<BsonValue, LocalDate> toLocalDate() {
        return x -> LocalDate.ofEpochDay(x.asInt64().getValue());
    }

    public static Function<Duration, BsonInt64> fromDuration() {
        return x -> new BsonInt64(x.toMillis());
    }

    public static Function<BsonValue, Duration> toDuration() {
        return x -> Duration.ofMillis(x.asInt64().getValue());
    }

    public static Function<ZoneId, BsonString> fromZoneId() {
        return x -> new BsonString(x.getId());
    }

    public static Function<BsonValue, ZoneId> toZoneId() {
        return x -> ZoneId.of(x.asString().getValue());
    }

    // other

    public static Function<Currency, BsonString> fromCurrency() {
        return x -> new BsonString(x.getCurrencyCode());
    }

    public static Function<BsonValue, Currency> toCurrency() {
        return x -> Currency.getInstance(x.asString().getValue());
    }

    // === UNITS OF MEASUREMENT ===

    public static Function<TemperatureUnit, BsonString> fromTemperatureUnit() {
        return x -> new BsonString(x.identifier());
    }

    public static Function<BsonValue, TemperatureUnit> toTemperatureUnit() {
        return x -> TemperatureUnit.of(x.asString().getValue()).orElseThrow();
    }

    public static Function<MassUnit, BsonString> fromMassUnit() {
        return x -> new BsonString(x.identifier());
    }

    public static Function<BsonValue, MassUnit> toMassUnit() {
        return x -> MassUnit.of(x.asString().getValue()).orElseThrow();
    }

}
