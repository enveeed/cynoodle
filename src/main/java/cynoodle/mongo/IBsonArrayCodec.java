/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
