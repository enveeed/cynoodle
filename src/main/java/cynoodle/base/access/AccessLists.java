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

package cynoodle.base.access;

import cynoodle.discord.GEntity;
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
