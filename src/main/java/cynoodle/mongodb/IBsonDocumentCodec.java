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
import cynoodle.mongodb.fluent.FluentDocument;
import org.bson.BSONException;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Simple {@link cynoodle.mongodb.fluent.Codec Codec} implementation for {@link IBsonDocument} implementations.
 * @param <T> the type of the implementation
 */
public final class IBsonDocumentCodec<T extends IBsonDocument> implements Codec<T> {

    private final Supplier<T> instanceSupplier;

    // ===

    public IBsonDocumentCodec(@Nonnull Supplier<T> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    // ===

    @Override
    public T load(BsonValue bson) throws BSONException {

        T instance = this.instanceSupplier.get();

        try {
            instance.fromBson(FluentDocument.wrap(bson.asDocument()));
        } catch (BsonDataException e) {
            throw new BsonDataException("Implementation failed to load BSON Document!", e);
        }

        return instance;
    }

    @Override
    public BsonValue store(T object) throws BSONException {

        FluentDocument bson;

        try {
            bson = object.toBson();
        } catch (BsonDataException e) {
            throw new BsonDataException("Implementation failed to store BSON Document!", e);
        }

        return bson.asBson();
    }
}
