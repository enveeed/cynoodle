/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo.fluent;

import org.bson.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FluentArray implements FluentValue {

    private final BsonArray array;

    // ===

    private FluentArray(BsonArray array) {
        this.array = array;
    }

    // ===

    @Override
    public BsonType getType() {
        return BsonType.ARRAY;
    }

    @Override
    public BsonArray asBson() {
        return this.array;
    }

    // ===

    public int size() {
        return this.array.size();
    }

    // ===

    public FluentArray clear() {
        this.array.clear();
        return this;
    }

    public FluentArray remove(int index) {
        this.array.remove(index);
        return this;
    }

    // ===

    public InsertAPI<BsonValue> insert() {
        return new InsertAPI<>(v -> v);
    }

    public CollectAPI<BsonValue> collect() {
        return new CollectAPI<>(0, size(), v -> v);
    }

    // === INSERT / COLLECT ===

    /**
     * API for insertion of values into the array.
     * @param <T> value type
     */
    public final class InsertAPI<T> {

        private final Function<? super T, ? extends BsonValue> function;

        // ===

        private InsertAPI(Function<? super T, ? extends BsonValue> function) {
            this.function = function;
        }

        // ===

        public <V> InsertAPI<V> as(Function<? super V, ? extends BsonValue> function) {
            return new InsertAPI<>(function);
        }

        public <V> InsertAPI<V> asNullable(Function<? super V, ? extends BsonValue> function) {
            return new InsertAPI<>(v -> v == null ? BsonNull.VALUE : function.apply(v));
        }

        public <V> InsertAPI<V> map(Function<? super V, ? extends T> function) {
            return new InsertAPI<>(function.andThen(this.function));
        }

        //

        public InsertAPI<FluentDocument> asDocument() {
            return as(FluentDocument::asBson);
        }

        public InsertAPI<FluentArray> asArray() {
            return as(FluentArray::asBson);
        }

        //

        public InsertAPI<String> asString() {
            return as(BsonString::new);
        }

        public InsertAPI<Integer> asInteger() {
            return as(BsonInt32::new);
        }

        public InsertAPI<Long> asLong() {
            return as(BsonInt64::new);
        }

        public InsertAPI<Double> asDouble() {
            return as(BsonDouble::new);
        }

        public InsertAPI<Boolean> asBoolean() {
            return as(BsonBoolean::new);
        }

        //

        public InsertAPI<FluentDocument> asDocumentNullable() {
            return as(x -> x == null ? BsonNull.VALUE : x.asBson());
        }

        public InsertAPI<FluentArray> asArrayNullable() {
            return as(x -> x == null ? BsonNull.VALUE : x.asBson());
        }

        //

        public InsertAPI<String> asStringNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonString(x));
        }

        public InsertAPI<Integer> asIntegerNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonInt32(x));
        }

        public InsertAPI<Long> asLongNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonInt64(x));
        }

        public InsertAPI<Double> asDoubleNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonDouble(x));
        }

        public InsertAPI<Boolean> asBooleanNullable() {
            return as(x -> x == null ? BsonNull.VALUE : new BsonBoolean(x));
        }

        // ===

        public FluentArray at(int index, T value) {
            array.add(index, this.function.apply(value));
            return FluentArray.this;
        }

        //

        public FluentArray atStart(T value) {
            return at(0, value);
        }

        public FluentArray atEnd(T value) {
            return at(size(), value);
        }

        // ===

        public FluentArray at(int index, Collection<T> values) {
            array.addAll(index, values.stream().map(function).collect(Collectors.toSet()));
            return FluentArray.this;
        }

        public FluentArray atStart(Collection<T> values) {
            return at(0, values);
        }

        public FluentArray atEnd(Collection<T> values) {
            return at(size(), values);
        }

    }

    //

    /**
     * API to collect values from the array.
     * @param <T> the value type
     */
    public final class CollectAPI<T> {

        private final int from; // TODO give this a use
        private final int to; // TODO give this a use
        private final Function<? super BsonValue, ? extends T> function;

        // ===

        private CollectAPI(int from, int to, Function<? super BsonValue, ? extends T> function) {
            this.from = from;
            this.to = to;
            this.function = function;
        }

        // ===

        public <V> CollectAPI<V> as(Function<? super BsonValue, ? extends V> function) {
            return new CollectAPI<>(this.from, this.to, function);
        }

        public <V> CollectAPI<V> asNullable(Function<? super BsonValue, ? extends V> function) {
            return new CollectAPI<>(this.from, this.to, value -> value.isNull() ? null : function.apply(value));
        }

        public <V> CollectAPI<V> map(Function<? super T, ? extends V> function) {
            return new CollectAPI<>(this.from, this.to, this.function.andThen(function));
        }

        //

        public CollectAPI<FluentDocument> asDocument() {
            return as(value -> FluentDocument.wrap(value.asDocument()));
        }

        public CollectAPI<FluentArray> asArray() {
            return as(value -> FluentArray.wrap(value.asArray()));
        }

        //

        public CollectAPI<String> asString() {
            return as(value -> value.asString().getValue());
        }

        public CollectAPI<Integer> asInteger() {
            return as(value -> value.asInt32().getValue());
        }

        public CollectAPI<Long> asLong() {
            return as(value -> value.asInt64().getValue());
        }

        public CollectAPI<Double> asDouble() {
            return as(value -> value.asDouble().getValue());
        }

        public CollectAPI<Boolean> asBoolean() {
            return as(value -> value.asBoolean().getValue());
        }

        //

        public CollectAPI<FluentDocument> asDocumentNullable() {
            return as(value -> value.isNull() ? null : FluentDocument.wrap(value.asDocument()));
        }

        public CollectAPI<FluentArray> asArrayNullable() {
            return as(value -> value.isNull() ? null : FluentArray.wrap(value.asArray()));
        }

        //

        public CollectAPI<String> asStringNullable() {
            return as(value -> value.isNull() ? null : value.asString().getValue());
        }

        public CollectAPI<Integer> asIntegerNullable() {
            return as(value -> value.isNull() ? null : value.asInt32().getValue());
        }

        public CollectAPI<Long> asLongNullable() {
            return as(value -> value.isNull() ? null : value.asInt64().getValue());
        }

        public CollectAPI<Double> asDoubleNullable() {
            return as(value -> value.isNull() ? null : value.asDouble().getValue());
        }

        public CollectAPI<Boolean> asBooleanNullable() {
            return as(value -> value.isNull() ? null : value.asBoolean().getValue());
        }

        // ===

        public <C extends Collection<T>> C into(C collection) {
            array.forEach(value -> collection.add(function.apply(value)));
            return collection;
        }

        public <C extends Collection<T>> C intoOr(C collection, C fallback) {
            if(size() == 0) return fallback;
            else return into(collection);
        }

        //

        public List<T> toList() {
            return into(new ArrayList<>());
        }

        public List<T> toListOr(List<T> fallback) {
            return intoOr(new ArrayList<>(), fallback);
        }

        //

        public Set<T> toSet() {
            return into(new HashSet<>());
        }

        public Set<T> toSetOr(Set<T> fallback) {
            return intoOr(new HashSet<>(), fallback);
        }

        //

        public <K> Map<K, T> toMap(Function<T, K> keyExtractor) {
            Map<K, T> map = new HashMap<>();
            array.forEach(value -> {
                T data = function.apply(value);
                K key = keyExtractor.apply(data);
                map.put(key, data);
            });
            return map;
        }

        public <K> Map<K, T> toMapOr(Function<T, K> keyExtractor, Map<K, T> fallback) {
            if(size() == 0) return fallback;
            else return toMap(keyExtractor);
        }
    }

    // === SET / GET ===

    /**
     * API for getting values at a specific index from the array.
     * @param <T> the value type
     */
    public final class GetAPI<T> {

        // TODO unify this with the GetAPI class from FluentDocument to avoid code duplication

        private final int index;
        private final Function<? super BsonValue, ? extends T> function;

        // ===

        private GetAPI(int index, Function<? super BsonValue, ? extends T> function) {
            this.index = index;
            this.function = function;
        }

        // ===

        public <V> GetAPI<V> as(Function<? super BsonValue, ? extends V> function) {
            return new GetAPI<>(this.index, function);
        }

        public <V> GetAPI<V> map(Function<? super T, ? extends V> function) {
            return new GetAPI<>(this.index, this.function.andThen(function));
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
            if((index < 0 || index >= size())) throw new NoSuchElementException("No value at index " + this.index + "!");
            return this.function.apply(array.get(index));
        }

        public T or(T fallback) {
            if((index < 0 || index >= size())) return fallback;
            return this.function.apply(array.get(index));
        }

        //

        public Optional<T> optional() {
            if((index < 0 || index >= size())) return Optional.empty();
            else return Optional.of(this.function.apply(array.get(index)));
        }
    }

    // ===

    public static FluentArray wrap(BsonArray array) {
        return new FluentArray(array);
    }

    public static FluentArray wrapNew() {
        return new FluentArray(new BsonArray());
    }
}
