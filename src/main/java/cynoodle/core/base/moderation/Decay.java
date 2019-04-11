/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.api.Checks;
import cynoodle.core.api.parser.Parser;
import cynoodle.core.api.parser.TimeParsers;
import cynoodle.core.mongo.fluent.FluentValues;
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
