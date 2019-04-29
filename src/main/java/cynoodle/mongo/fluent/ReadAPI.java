/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo.fluent;

import org.bson.BsonValue;

import java.util.function.Function;

interface ReadAPI<V> extends API {

    <T> ReadAPI<T> as(Function<? super BsonValue, ? extends T> function);

    default <T> ReadAPI<T> asNullable(Function<? super BsonValue, ? extends T> function) {
        return as(x -> x.isNull() ? null : function.apply(x));
    }

    //

    <T> ReadAPI<T> map(Function<? super V, ? extends T> function);

    // ===

    @Override
    default ReadAPI<FluentDocument> asDocument() {
        return as(value -> FluentDocument.wrap(value.asDocument()));
    }

    @Override
    default ReadAPI<FluentArray> asArray() {
        return as(value -> FluentArray.wrap(value.asArray()));
    }

    //

    @Override
    default ReadAPI<String> asString() {
        return as(value -> value.asString().getValue());
    }

    @Override
    default ReadAPI<Integer> asInteger() {
        return as(value -> value.asInt32().getValue());
    }

    @Override
    default ReadAPI<Long> asLong() {
        return as(value -> value.asInt64().getValue());
    }

    @Override
    default ReadAPI<Double> asDouble() {
        return as(value -> value.asDouble().getValue());
    }

    @Override
    default ReadAPI<Boolean> asBoolean() {
        return as(value -> value.asBoolean().getValue());
    }

    //

    @Override
    default ReadAPI<FluentDocument> asDocumentNullable() {
        return asNullable(value -> FluentDocument.wrap(value.asDocument()));
    }

    @Override
    default ReadAPI<FluentArray> asArrayNullable() {
        return asNullable(value -> FluentArray.wrap(value.asArray()));
    }

    //

    @Override
    default ReadAPI<String> asStringNullable() {
        return asNullable(value -> value.asString().getValue());
    }

    @Override
    default ReadAPI<Integer> asIntegerNullable() {
        return asNullable(value -> value.asInt32().getValue());
    }

    @Override
    default ReadAPI<Long> asLongNullable() {
        return asNullable(value -> value.asInt64().getValue());
    }

    @Override
    default ReadAPI<Double> asDoubleNullable() {
        return asNullable(value -> value.asDouble().getValue());
    }

    @Override
    default ReadAPI<Boolean> asBooleanNullable() {
        return asNullable(value -> value.asBoolean().getValue());
    }
}
