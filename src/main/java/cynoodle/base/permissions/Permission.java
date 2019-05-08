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

package cynoodle.base.permissions;

import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.entities.EntityReference;
import cynoodle.entities.EntityType;
import cynoodle.mongo.fluent.Codec;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * A Permission, which defines access to a specific resource or functionality.
 */
@EIdentifier(PermissionsModule.IDENTIFIER + ":permission")
public final class Permission extends GEntity {
    private Permission() {}

    static final EntityType<Permission> TYPE = EntityType.of(Permission.class);

    // ===

    public boolean test(@Nonnull Member member) {
        return Permissions.get().test(member, this);
    }

    // ===

    /**
     * Convenience method to obtain the {@link Codec} for an {@link EntityReference}
     * of a {@link Permission}.
     * @return the codec for a permission entity reference
     */
    @Nonnull
    public static Codec<EntityReference<Permission>> referenceCodec() {
        return Permissions.get().codecPermissionReference();
    }
}
