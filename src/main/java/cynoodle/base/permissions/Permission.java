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
import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * A Permission, which defines access to a specific resource or functionality.
 */
@EIdentifier(PermissionsModule.IDENTIFIER + ":permission")
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

    /**
     * Additional meta for permissions, e.g. for back referencing related resources.
     * Codec is defined via {@link PermissionType}.
     */
    private PermissionMeta meta;

    // ===

    @Nonnull
    public String getPermissionTypeName() {
        return permissionType;
    }

    void setPermissionTypeName(@Nonnull String type) {
        this.permissionType = type;
    }

    public boolean getStatusDefault() {
        return this.statusDefault;
    }

    void setStatusDefault(boolean status) {
        this.statusDefault = status;
    }

    //

    @Nonnull
    public PermissionMeta getMeta() {
        return this.meta;
    }

    void setMeta(@Nonnull PermissionMeta meta) {
        this.meta = meta;
    }

    //

    @Nonnull
    public PermissionType getPermissionType() {
        return Permissions.get()
                .findType(this.permissionType)
                .orElseThrow(() -> new IllegalStateException("Permission "
                        + this.getID() + " has unknown (unregistered) type of " + this.permissionType + "!"));
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
        this.meta = data.getAt("meta").as(getPermissionType().getMetaCodec()).value();

    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("type").asString().to(this.permissionType);
        data.setAt("status_default").asBoolean().to(this.statusDefault);

        // TODO replace this dangerous codec cast
        //  (it works because its always a subtype of PermissionMeta anyways so it does not matter)
        //  with fixed generics in the fluent Bson API which allow acceptance of wildcard codec in one direction or the other
        //  / any other solution in case its not possible with java generics (i'm a bit confused there still, its a mess honestly)

        data.setAt("meta").as((Codec<PermissionMeta>) getPermissionType().getMetaCodec()).to(this.meta);

        return data;
    }
}
