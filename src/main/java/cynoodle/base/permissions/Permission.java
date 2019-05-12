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
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentDocument;
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

    /**
     * The {@link PermissionType} key for this permission.
     */
    private String permissionType;

    // ===

    @Nonnull
    public String getPermissionTypeKey() {
        return this.permissionType;
    }

    @Nonnull
    public PermissionType getPermissionType() {
        // NOTE: This throws in case this is a orphan permission.
        return Permissions.get().getTypeRegistry()
                .require(this.permissionType);
    }

    //

    void setPermissionTypeKey(@Nonnull String permissionType) {
        this.permissionType = permissionType;
    }

    // ===

    // These methods all depend on the permission type

    @Nonnull
    public String getName() {
        return getPermissionType()
                .getPermissionName(this);
    }

    // ...

    // === INTERNAL ===

    // this is the case if the type is no longer known
    // TODO also introduce a way to detect if the type no longer controls this permission
    boolean isOrphan() {
        return !Permissions.get().getTypeRegistry()
                .contains(getPermissionTypeKey());
    }

    // ===

    public boolean test(@Nonnull Member member) {
        return Permissions.get()
                .test(member, this);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.permissionType = source.getAt("permission_type").asString().value();
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("permission_type").asString().to(this.permissionType);

        return data;
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
