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

package cynoodle.mongodb;

import cynoodle.mongodb.fluent.Codec;
import org.bson.BSONException;
import org.bson.BsonValue;

import java.util.function.Function;

/**
 * Simple codec to map one type of codec to another.
 * @param <T> the type of this codec
 * @param <V> the target codec type
 */
public final class MappingCodec<T, V> implements Codec<T> {

    private final Codec<V> targetCodec;
    private final Function<V, T> load;
    private final Function<T, V> store;

    // ===

    public MappingCodec(Codec<V> targetCodec, Function<V, T> load, Function<T, V> store) {
        this.targetCodec = targetCodec;
        this.load = load;
        this.store = store;
    }

    // ===

    @Override
    public T load(BsonValue bson) throws BSONException {
        return this.load.apply(targetCodec.load(bson));
    }

    @Override
    public BsonValue store(T object) throws BSONException {
        return targetCodec.store(this.store.apply(object));
    }
}
