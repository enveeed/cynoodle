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

package cynoodle.base.moderation;

import cynoodle.util.Checks;
import cynoodle.util.parsing.Parser;
import cynoodle.util.parsing.TimeParsers;
import cynoodle.mongodb.fluent.FluentValues;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

/**
 * The decay policy for a {@link Strike}.
 * Is either a duration or never.
 */
public final class Decay {

    private final Duration duration;

    // ===

    private Decay(@Nullable Duration duration) {
        this.duration = duration;
    }

    // ===

    public boolean isNever() {
        return duration == null;
    }

    public boolean isDecayable() {
        return duration != null;
    }

    // ===

    @Nonnull
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    // ===

    @Nonnull
    public static Decay never() {
        return new Decay(null);
    }

    @Nonnull
    public static Decay of(@Nonnull Duration duration) {
        Checks.notNull(duration, "duration");
        return new Decay(duration);
    }

    // ===

    @Nonnull
    public static Function<Decay, BsonValue> toBson() {
        return decay -> decay.isNever() ? BsonNull.VALUE : new BsonInt64(decay.getDuration().orElseThrow().toMillis());
    }

    @Nonnull
    public static Function<BsonValue, Decay> fromBson() {
        return value -> value.isNull() ? Decay.never() : Decay.of(FluentValues.toDuration().apply(value));
    }

    // ===

    @Nonnull
    public static Parser<Decay> parser() {
        return input -> {
            if (input.equalsIgnoreCase("never")) return Decay.never();
            else return Decay.of(TimeParsers.parseDuration().parse(input));
        };
    }
}
