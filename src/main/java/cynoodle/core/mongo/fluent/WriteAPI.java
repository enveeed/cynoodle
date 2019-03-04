/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo.fluent;

import org.bson.*;

import java.util.function.Function;

interface WriteAPI<V> extends API {

    <T> WriteAPI<T> as(Function<? super T, ? extends BsonValue> function);

    default <T> WriteAPI<T> asNullable(Function<? super T, ? extends BsonValue> function) {
        return as(x -> x == null ? BsonNull.VALUE : function.apply(x));
    }

    //

    <T> WriteAPI<T> map(Function<? super T, ? extends V> function);

    // ===

    @Override
    default WriteAPI<FluentDocument> asDocument() {
        return as(FluentDocument::asBson);
    }

    @Override
    default WriteAPI<FluentArray> asArray() {
        return as(FluentArray::asBson);
    }

    //

    @Override
    default WriteAPI<String> asString() {
        return as(BsonString::new);
    }

    @Override
    default WriteAPI<Integer> asInteger() {
        return as(BsonInt32::new);
    }

    @Override
    default WriteAPI<Long> asLong() {
        return as(BsonInt64::new);
    }

    @Override
    default WriteAPI<Double> asDouble() {
        return as(BsonDouble::new);
    }

    @Override
    default WriteAPI<Boolean> asBoolean() {
        return as(BsonBoolean::new);
    }

    //

    @Override
    default WriteAPI<FluentDocument> asDocumentNullable() {
        return asNullable(FluentDocument::asBson);
    }

    @Override
    default WriteAPI<FluentArray> asArrayNullable() {
        return asNullable(FluentArray::asBson);
    }

    //

    @Override
    default WriteAPI<String> asStringNullable() {
        return asNullable(BsonString::new);
    }

    @Override
    default WriteAPI<Integer> asIntegerNullable() {
        return asNullable(BsonInt32::new);
    }

    @Override
    default WriteAPI<Long> asLongNullable() {
        return asNullable(BsonInt64::new);
    }

    @Override
    default WriteAPI<Double> asDoubleNullable() {
        return asNullable(BsonDouble::new);
    }

    @Override
    default WriteAPI<Boolean> asBooleanNullable() {
        return asNullable(BsonBoolean::new);
    }
}
