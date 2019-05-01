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