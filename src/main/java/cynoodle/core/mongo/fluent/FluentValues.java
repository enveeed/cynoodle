/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo.fluent;

import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonValue;

import java.time.Instant;
import java.util.function.Function;

public final class FluentValues {
    private FluentValues() {}

    // ===

    public static Function<Instant, BsonInt64> fromInstant() {
        return x -> new BsonInt64(x.toEpochMilli());
    }

    public static Function<Instant, BsonValue> fromInstantNullable() {
        return x -> x == null ? BsonNull.VALUE : new BsonInt64(x.toEpochMilli());
    }

    public static Function<BsonValue, Instant> toInstant() {
        return x -> Instant.ofEpochMilli(x.asInt64().getValue());
    }

    public static Function<BsonValue, Instant> toInstantNullable() {
        return x -> x.isNull() ? null :Instant.ofEpochMilli(x.asInt64().getValue());
    }
}
