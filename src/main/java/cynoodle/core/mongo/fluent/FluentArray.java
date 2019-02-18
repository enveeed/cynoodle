/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo.fluent;

import org.bson.BsonArray;
import org.bson.BsonType;

public final class FluentArray implements FluentValue {

    private final BsonArray array;

    // ===

    private FluentArray(BsonArray array) {
        this.array = array;
    }

    // ===

    @Override
    public BsonType getType() {
        return BsonType.ARRAY;
    }

    @Override
    public BsonArray asBson() {
        return this.array;
    }

    // ===

    public int size() {
        return this.array.size();
    }

    // ===

    public static FluentArray wrap(BsonArray array) {
        return new FluentArray(array);
    }
}
