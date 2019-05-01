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
import cynoodle.entities.EntityType;
import cynoodle.mongo.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * A Permission, which defines access to a specific resource or functionality.
 */
@EIdentifier("base:permissions:permission")
public final class Permission extends GEntity {
    private Permission() {}

    static final EntityType<Permission> TYPE = EntityType.of(Permission.class);

    // ===

    /**
     * The type name of this permission, decides which {@link PermissionType} instance will be used at runtime.
     */
    private String permissionType;

    /**
     * The default status of this permission, which {@link Permissions#test(Member, Permission)} will
     * fall back to in case the permission is defined nowhere, for neither allow or deny.
     */
    private boolean statusDefault = false;

    // ===

    @Nonnull
    public String getPermissionTypeName() {
        return permissionType;
    }

    void setPermissionTypeName(@Nonnull String type) {
        this.permissionType = type;
    }

    //

    public boolean getStatusDefault() {
        return this.statusDefault;
    }

    void setStatusDefault(boolean status) {
        this.statusDefault = status;
    }

    //

    @Nonnull
    public PermissionType getPermissionType() {
        return Permissions.get()
                .findType(this.permissionType)
                .orElseThrow(() -> new IllegalStateException("Permission "
                        + this.getID() + " has unknown type of " + this.permissionType + "!"));
    }

    @Nonnull
    public String getDisplayName() {
        return this.getPermissionType().getDisplayName(this);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {

        this.permissionType = data.getAt("type").asString().value();
        this.statusDefault = data.getAt("status_default").asBoolean().or(this.statusDefault);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("type").asString().to(this.permissionType);
        data.setAt("status_default").asBoolean().to(this.statusDefault);

        return data;
    }
}
