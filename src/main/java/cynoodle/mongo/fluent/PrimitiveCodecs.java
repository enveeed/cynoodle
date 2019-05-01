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

import okio.ByteString;
import org.bson.*;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Contains {@link Codec Codecs} for all BSON primitives.
 */
public final class PrimitiveCodecs {
    private PrimitiveCodecs() {}

    // ===

    // NOTE: The order follows the order of all BSON Types in the official documentation
    // and does not include deprecated types, Null and nested (Array and Document / Object) types.
    // https://docs.mongodb.com/manual/reference/bson-types/

    // 1 Double
    public static Codec<Double> forDouble() {
        return new Codec<>() {
            @Override
            public Double load(BsonValue bson) throws BSONException {
                return bson.asDouble().getValue();
            }

            @Override
            public BsonValue store(Double object) throws BSONException {
                return new BsonDouble(object);
            }
        };
    }

    // 2 String
    public static Codec<String> forString() {
        return new Codec<>() {
            @Override
            public String load(BsonValue bson) throws BSONException {
                return bson.asString().getValue();
            }

            @Override
            public BsonValue store(String object) throws BSONException {
                return new BsonString(object);
            }
        };
    }

    // 3 Object / Document (skipped)
    // 4 Array (skipped)

    // 5 Binary
    public static Codec<ByteString> forBinary() {
        return new Codec<>() {
            @Override
            public ByteString load(BsonValue bson) throws BSONException {
                return ByteString.of(bson.asBinary().getData());
            }

            @Override
            public BsonValue store(ByteString object) throws BSONException {
                return new BsonBinary(object.toByteArray());
            }
        };
    }

    // 6 Undefined (skipped)

    // 7 ObjectId
    public static Codec<ObjectId> forObjectId() {
        return new Codec<>() {
            @Override
            public ObjectId load(BsonValue bson) throws BSONException {
                return bson.asObjectId().getValue();
            }

            @Override
            public BsonValue store(ObjectId object) throws BSONException {
                return new BsonObjectId(object);
            }
        };
    }

    // 8 Boolean
    public static Codec<Boolean> forBoolean() {
        return new Codec<>() {
            @Override
            public Boolean load(BsonValue bson) throws BSONException {
                return bson.asBoolean().getValue();
            }

            @Override
            public BsonValue store(Boolean object) throws BSONException {
                return new BsonBoolean(object);
            }
        };
    }

    // 9 Date ("Date Time")
    // TODO maybe reconsider using Instant here
    public static Codec<Instant> forDate() {
        return new Codec<>() {
            @Override
            public Instant load(BsonValue bson) throws BSONException {
                return Instant.ofEpochMilli(bson.asDateTime().getValue());
            }

            @Override
            public BsonValue store(Instant object) throws BSONException {
                return new BsonDateTime(object.toEpochMilli());
            }
        };
    }

    // 10 Null (skipped)

    // TODO 11 Regular Expression (find a way to represent a regex without compiling it into a Pattern)

    // 12 DBPointer (skipped)

    // TODO 13 JavaScript

    // 14 Symbol (skipped)

    // TODO 15 JavaScript (with scope)

    // 16 32-bit integer
    public static Codec<Integer> forInt32() {
        return new Codec<>() {
            @Override
            public Integer load(BsonValue bson) throws BSONException {
                return bson.asInt32().getValue();
            }

            @Override
            public BsonValue store(Integer object) throws BSONException {
                return new BsonInt32(object);
            }
        };
    }

    // 17 Timestamp
    public static Codec<Instant> forTimestamp() {
        return new Codec<>() {
            @Override
            public Instant load(BsonValue bson) throws BSONException {
                return Instant.ofEpochMilli(bson.asTimestamp().getValue());
            }

            @Override
            public BsonValue store(Instant object) throws BSONException {
                return new BsonTimestamp(object.toEpochMilli());
            }
        };
    }

    // 18 64-bit integer
    public static Codec<Long> forInt64() {
        return new Codec<>() {
            @Override
            public Long load(BsonValue bson) throws BSONException {
                return bson.asInt64().getValue();
            }

            @Override
            public BsonValue store(Long object) throws BSONException {
                return new BsonInt64(object);
            }
        };
    }

    // 19 128-bit decimal
    public static Codec<Decimal128> forDecimal128() {
        return new Codec<>() {
            @Override
            public Decimal128 load(BsonValue bson) throws BSONException {
                return bson.asDecimal128().getValue();
            }

            @Override
            public BsonValue store(Decimal128 object) throws BSONException {
                return new BsonDecimal128(object);
            }
        };
    }
}
