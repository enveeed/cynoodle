/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo;

import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentDocument;
import org.bson.BSONException;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Simple {@link cynoodle.mongo.fluent.Codec Codec} implementation for {@link IBsonDocument} implementations.
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
