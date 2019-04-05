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
        return EAccessList.TYPE.create(parent);
    }

    @Nonnull
    public static AccessList create(@Nonnull GEntity parent, @Nonnull AccessList.Status defaultStatus) {
        return EAccessList.TYPE.create(parent, list -> list.setDefaultStatus(defaultStatus));
    }

    // === BSON ===

    @Nonnull
    public static Function<BsonValue, AccessList> load(@Nonnull GEntity parent) {
        return EAccessList.TYPE.load(parent).andThen(x -> x);
    }

    @Nonnull
    public static Function<AccessList, BsonValue> store() {
        return EAccessList.TYPE.store().compose(x -> (EAccessList) x);
    }
}
