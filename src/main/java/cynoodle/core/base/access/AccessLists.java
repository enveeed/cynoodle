/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.discord.GEntity;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Static utility for {@link AccessList AccessLists}.
 */
public final class AccessLists {
    private AccessLists() {}

    // ===

    @Nonnull
    public static AccessList create(@Nonnull GEntity parent) {
        return AccessList.TYPE.create(parent);
    }

    // === BSON ===

    @Nonnull
    public static Function<BsonValue, AccessList> load(@Nonnull GEntity parent) {
        return AccessList.TYPE.load(parent);
    }

    @Nonnull
    public static Function<AccessList, BsonValue> store() {
        return AccessList.TYPE.store();
    }
}
