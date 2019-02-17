/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo;

import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;

/**
 * Fluent pendant to {@link BsonData}.
 */
public interface FluentBsonData extends BsonData {

    void fromFluentBson(@Nonnull FluentDocument data) throws BsonDataException;

    FluentDocument toFluentBson() throws BsonDataException;

    // ===

    @Override
    default void fromBson(@Nonnull BsonDocument data) throws BsonDataException {
        this.fromFluentBson(FluentDocument.wrap(data));
    }

    @Nonnull
    @Override
    default BsonDocument toBson() throws BsonDataException {
        return toFluentBson().asBson();
    }
}
