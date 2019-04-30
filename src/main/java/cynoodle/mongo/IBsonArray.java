/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo;

import cynoodle.mongo.fluent.FluentArray;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * {@link FluentArray} conversion interface.
 */
public interface IBsonArray {

    void fromBson(@Nonnull FluentArray data) throws BSONException;

    @Nonnull
    FluentArray toBson() throws BSONException;

}
