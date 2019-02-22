/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo;

import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * Types implementing this interface have the ability to output their state
 * as MongoDB BSON or update it from MongoDB BSON.
 */
public interface Bsonable {

    /**
     * Update the state of this object with the state given as BSON.
     * @param data the BSON data
     * @throws BsonDataException if the BSON is invalid or doesnt match the expected format
     * or there was an issue with updating the state
     */
    default void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        //
    }

    /**
     * Output the state of this object as BSON.
     * @return the BSON data
     * @throws BsonDataException if there was an issue with outputting the state
     */
    @Nonnull
    default FluentDocument toBson() throws BsonDataException {
        return FluentDocument.wrapNew();
    }
}
