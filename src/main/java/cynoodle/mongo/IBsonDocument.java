/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo;

import cynoodle.mongo.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * {@link FluentDocument} conversion interface.
 */
public interface IBsonDocument {

    void fromBson(@Nonnull FluentDocument data) throws BSONException;

    @Nonnull
    FluentDocument toBson() throws BSONException;
}
