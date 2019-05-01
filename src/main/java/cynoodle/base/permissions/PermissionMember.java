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

import cynoodle.discord.MEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.entities.EntityType;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * Permissions of a {@link net.dv8tion.jda.api.entities.Member Member}.
 *
 * Permissions defined explicitly for a Member always take priority over any {@link PermissionRole Role Permissions}
 * the member may have according to their Discord Roles.
 */
@EIdentifier(PermissionsModule.IDENTIFIER + ":permission_member")
public final class PermissionMember extends MEntity {
    private PermissionMember() {}

    static final EntityType<PermissionMember> TYPE = EntityType.of(PermissionMember.class);

    // ===

    private PermissionContainer container = new PermissionContainer();

    // ===

    @Nonnull
    public PermissionContainer getPermissions() {
        return this.container;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.container = source.getAt("permissions").as(PermissionContainer.codec()).or(this.container);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("permissions").as(PermissionContainer.codec()).to(this.container);

        return data;
    }
}
