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
