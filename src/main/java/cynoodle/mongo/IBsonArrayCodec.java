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

package cynoodle.mongo;

import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentArray;
import org.bson.BSONException;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Simple {@link cynoodle.mongo.fluent.Codec Codec} implementation for {@link IBsonArray} implementations.
 * @param <T> the type of the implementation
 */
public final class IBsonArrayCodec<T extends IBsonArray> implements Codec<T> {

    private final Supplier<T> instanceSupplier;

    // ===

    public IBsonArrayCodec(@Nonnull Supplier<T> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    // ===

    @Override
    public T load(BsonValue bson) throws BSONException {

        T instance = this.instanceSupplier.get();

        try {
            instance.fromBson(FluentArray.wrap(bson.asArray()));
        } catch (BsonDataException e) {
            throw new BsonDataException("Implementation failed to load BSON Array!", e);
        }

        return instance;
    }

    @Override
    public BsonValue store(T object) throws BSONException {

        FluentArray bson;

        try {
            bson = object.toBson();
        } catch (BsonDataException e) {
            throw new BsonDataException("Implementation failed to store BSON Array!", e);
        }

        return bson.asBson();
    }
}
