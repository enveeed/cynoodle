/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo.fluent;

import org.bson.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public final class FluentDocument implements FluentValue {

    private final BsonDocument document;

    // ===

    private FluentDocument(BsonDocument document) {
        this.document = document;
    }

    // ===

    @Override
    public BsonType getType() {
        return BsonType.DOCUMENT;
    }

    @Override
    public BsonDocument asBson() {
        return this.document;
    }

    // ===

    public AtAPI at(String key) {
        return new AtAPI(key);
    }

    // ===

    public SetAPI<BsonValue> setAt(String key) {
        return at(key).set();
    }

    public GetAPI<BsonValue> getAt(String key) {
        return at(key).get();
    }

    // ===

    public class AtAPI {

        protected final String key;

        // ===

        private AtAPI(String key) {
            this.key = key;
        }

        // ===

        public SetAPI<BsonValue> set() {
            return new SetAPI<>(this.key, v -> v);
        }

        public GetAPI<BsonValue> get() {
            return new GetAPI<>(this.key, v -> v);
        }

        // ===

        public boolean exists() {
            return document.containsKey(this.key);
        }

        // ===

        public FluentDocument remove() {
            document.remove(this.key);
            return FluentDocument.this;
        }
    }

    // ===

    public final class SetAPI<T> extends AtAPI {

        private final Function<? super T, ? extends BsonValue> function;

        // ===

        private SetAPI(String key, Function<? super T, ? extends BsonValue> function) {
            super(key);
            this.function = function;
        }

        // ===

        public <V> SetAPI<V> as(Function<? super V, ? extends BsonValue> function) {
            return new SetAPI<>(this.key, function);
        }

        public <V> SetAPI<V> map(Function<? super V, ? extends T> function) {
            return new SetAPI<>(this.key, function.andThen(this.function));
        }

        //

        public SetAPI<FluentDocument> asDocument() {
            return as(FluentDocument::asBson);
        }

        public SetAPI<FluentArray> asArray() {
            return as(FluentArray::asBson);
        }

        //

        public SetAPI<String> asString() {
            return as(BsonString::new);
        }

        public SetAPI<Integer> asInteger() {
            return as(BsonInt32::new);
        }

        public SetAPI<Long> asLong() {
            return as(BsonInt64::new);
        }

        public SetAPI<Double> asDouble() {
            return as(BsonDouble::new);
        }

        public SetAPI<Boolean> asBoolean() {
            return as(BsonBoolean::new);
        }

        //

        public SetAPI<FluentDocument> asDocumentNullable() {
            return as(x -> x == null ? BsonNull.VALUE : x.asBson());
        }

        public SetAPI<FluentArray> asArrayNullable() {
            return as(x -> x == null ? BsonNull.VALUE : x.asBson());
        }

        //

        public SetAPI<String> asStringNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonString(x));
        }

        public SetAPI<Integer> asIntegerNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonInt32(x));
        }

        public SetAPI<Long> asLongNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonInt64(x));
        }

        public SetAPI<Double> asDoubleNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonDouble(x));
        }

        public SetAPI<Boolean> asBooleanNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonBoolean(x));
        }

        // ===

        public FluentDocument to(T value) {
            document.put(this.key, this.function.apply(value));
            return FluentDocument.this;
        }

    }

    public final class GetAPI<T> extends AtAPI {

        private final Function<? super BsonValue, ? extends T> function;

        // ===

        private GetAPI(String key, Function<? super BsonValue, ? extends T> function) {
            super(key);
            this.function = function;
        }

        // ===

        public <V> GetAPI<V> as(Function<? super BsonValue, ? extends V> function) {
            return new GetAPI<>(this.key, function);
        }

        public <V> GetAPI<V> map(Function<? super T, ? extends V> function) {
            return new GetAPI<>(this.key, this.function.andThen(function));
        }

        //

        public GetAPI<FluentDocument> asDocument() {
            return as(value -> FluentDocument.wrap(value.asDocument()));
        }

        public GetAPI<FluentArray> asArray() {
            return as(value -> FluentArray.wrap(value.asArray()));
        }

        //

        public GetAPI<String> asString() {
            return as(value -> value.asString().getValue());
        }

        public GetAPI<Integer> asInteger() {
            return as(value -> value.asInt32().getValue());
        }

        public GetAPI<Long> asLong() {
            return as(value -> value.asInt64().getValue());
        }

        public GetAPI<Double> asDouble() {
            return as(value -> value.asDouble().getValue());
        }

        public GetAPI<Boolean> asBoolean() {
            return as(value -> value.asBoolean().getValue());
        }

        //

        public GetAPI<FluentDocument> asDocumentNullable() {
            return as(value -> value.isNull() ? null : FluentDocument.wrap(value.asDocument()));
        }

        public GetAPI<FluentArray> asArrayNullable() {
            return as(value -> value.isNull() ? null : FluentArray.wrap(value.asArray()));
        }

        //

        public GetAPI<String> asStringNullable() {
            return as(value -> value.isNull() ? null : value.asString().getValue());
        }

        public GetAPI<Integer> asIntegerNullable() {
            return as(value -> value.isNull() ? null : value.asInt32().getValue());
        }

        public GetAPI<Long> asLongNullable() {
            return as(value -> value.isNull() ? null : value.asInt64().getValue());
        }

        public GetAPI<Double> asDoubleNullable() {
            return as(value -> value.isNull() ? null : value.asDouble().getValue());
        }

        public GetAPI<Boolean> asBooleanNullable() {
            return as(value -> value.isNull() ? null : value.asBoolean().getValue());
        }

        // ===

        public T value() throws NoSuchElementException {
            BsonValue value = document.get(this.key);
            if(value == null) throw new NoSuchElementException("No value at key \"" + this.key + "\"!");
            return this.function.apply(value);
        }

        public T or(T fallback) {
            BsonValue value = document.get(this.key);
            if(value == null) return fallback;
            return this.function.apply(value);
        }

        //

        public Optional<T> optional() {
            BsonValue value = document.get(this.key);
            if(value == null) return Optional.empty();
            else return Optional.of(this.function.apply(value));
        }
    }

    // ===

    public static FluentDocument wrap(BsonDocument bson) {
        return new FluentDocument(bson);
    }

    public static FluentDocument wrapNew() {
        return new FluentDocument(new BsonDocument());
    }
}
