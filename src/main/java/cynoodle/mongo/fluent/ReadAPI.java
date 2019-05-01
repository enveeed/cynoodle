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
