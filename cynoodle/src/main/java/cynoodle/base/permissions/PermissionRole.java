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

import com.mongodb.client.model.Filters;
import cynoodle.discord.DiscordPointer;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.Entity;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.Role;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;

/**
 * Permissions of a {@link net.dv8tion.jda.api.entities.Role Role}.
 */
@EIdentifier(PermissionsModule.IDENTIFIER + ":permission_role")
public final class PermissionRole extends Entity {
    private PermissionRole() {}

    static final EntityType<PermissionRole> TYPE = EntityType.of(PermissionRole.class);

    // ===

    private DiscordPointer role;

    private PermissionContainer container = new PermissionContainer();

    // ===

    void setRole(@Nonnull DiscordPointer role) {
        this.role = role;
    }

    @Nonnull
    public Role getRole() {
        return this.role.requireRole();
    }

    //

    @Nonnull
    public PermissionContainer getPermissions() {
        return this.container;
    }

    // ===

    @Nonnull
    static Bson filterRole(@Nonnull Role role) {
        return Filters.eq("role", role.getIdLong());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        this.role = data.getAt("role").as(DiscordPointer.codec()).or(this.role);
        this.container = data.getAt("permissions").as(PermissionContainer.codec()).or(this.container);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("role").as(DiscordPointer.codec()).to(this.role);
        data.setAt("permissions").as(PermissionContainer.codec()).to(this.container);

        return data;
    }
}
