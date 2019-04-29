/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo.fluent;

interface API {

    API asDocument();

    API asArray();

    //

    API asString();

    API asInteger();

    API asLong();

    API asDouble();

    API asBoolean();

    //

    API asDocumentNullable();

    API asArrayNullable();

    //

    API asStringNullable();

    API asIntegerNullable();

    API asLongNullable();

    API asDoubleNullable();

    API asBooleanNullable();

}
