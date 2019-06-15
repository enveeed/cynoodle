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

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Contains {@link Codec Codecs} for common types within Java.
 */
public final class MoreCodecs {
    private MoreCodecs() {}

    // === MAP ===

    // NOTE: This is only for maps which are value-orientated, that means that its representable by a collection of values
    // and the values include the key which can be extracted during loading.
    @Nonnull
    public static <K, V> Codec<Map<K, V>> forValueMap(@Nonnull Codec<V> valueCodec, @Nonnull Function<V, K> keyExtractor) {
        return new Codec<>() {
            @Override
            public Map<K, V> load(BsonValue bson) throws BSONException {

                HashMap<K, V> map = new HashMap<>();

                bson.asArray().forEach(bsonValue -> {
                    V value = valueCodec.load(bsonValue);
                    K key = keyExtractor.apply(value);
                    map.put(key, value);
                });

                return Collections.unmodifiableMap(map);
            }

            @Override
            public BsonValue store(Map<K, V> object) throws BSONException {

                BsonArray array = new BsonArray();

                object.forEach((k, v) -> array.add(valueCodec.store(v)));

                return array;
            }
        };
    }

    // === TIME ===

    @Nonnull
    public static Codec<Duration> forDuration() {
        return new Codec<>() {
            @Override
            public Duration load(BsonValue bson) throws BSONException {
                return Duration.ofMillis(bson.asInt64().getValue());
            }

            @Override
            public BsonValue store(Duration object) throws BSONException {
                return new BsonInt64(object.toMillis());
            }
        };
    }

    @Nonnull
    public static Codec<Instant> forInstant() {
        return new Codec<>() {
            @Override
            public Instant load(BsonValue bson) throws BSONException {
                return Instant.ofEpochMilli(bson.asInt64().getValue());
            }

            @Override
            public BsonValue store(Instant object) throws BSONException {
                return new BsonInt64(object.toEpochMilli());
            }
        };
    }
}
