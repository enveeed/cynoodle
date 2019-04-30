/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo.fluent;

import org.bson.BSONException;
import org.bson.BsonValue;

import java.util.function.Function;

/**
 * A <b>Codec</b> provides a specific way to serialize an object to BSON ("<b>store</b>") and back ("<b>load</b>").
 * It also provides static methods to easily obtain functions to <b>store</b> or <b>load</b>.
 * @param <T> the object type
 */
public interface Codec<T> {

    /**
     * Load the given BSON value and produce an object representing that value,
     * according to this codec.
     * @param bson the BSON value
     * @return the object representing it
     * @throws BSONException if the object could not be created due to invalid format of the BSON value
     */
    T load(BsonValue bson) throws BSONException;

    /**
     * Store the given object as a BSON value, representing that object.
     * @param object the object
     * @return the BSON value representing it
     * @throws BSONException if the value could not be crated due to invalid format of the object
     */
    BsonValue store(T object) throws BSONException;

    // ===

    static <T> Function<BsonValue, T> load(Codec<T> codec) {
        return codec::load;
    }

    static <T> Function<T, BsonValue> store(Codec<T> codec) {
        return codec::store;
    }

}