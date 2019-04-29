/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo.fluent;

import org.bson.BsonType;
import org.bson.BsonValue;

public interface FluentValue {

    BsonType getType();

    BsonValue asBson();

}
