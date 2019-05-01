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

import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.entities.EntityManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central permission management.
 * Obtain instance via {@link #get()}, requires {@link PermissionsModule} to be active.
 */
public final class Permissions {

    Permissions() {}

    // ===

    private final GEntityManager<Permission> permissionEntityManager
            = new GEntityManager<>(Permission.TYPE);
    private final EntityManager<PermissionRole> roleEntityManager
            = new EntityManager<>(PermissionRole.TYPE);
    private final MEntityManager<PermissionMember> memberEntityManager
            = new MEntityManager<>(PermissionMember.TYPE);

    private final Map<String, PermissionType> typeRegistry
            = new HashMap<>();

    // ===

    GEntityManager<Permission> getPermissionEntityManager() {
        return this.permissionEntityManager;
    }

    // ===

    public void registerType(@Nonnull PermissionType type) {
        this.typeRegistry.put(type.getName(), type);
    }

    @Nonnull
    public Optional<PermissionType> findType(@Nonnull String name) {
        return Optional.ofNullable(this.typeRegistry.get(name));
    }

    // ===

    @Nonnull
    public Optional<Permission> getPermission(long id) {
        return this.permissionEntityManager.get(id);
    }

    //

    @Nonnull
    public Permission createPermission(@Nonnull Guild guild, @Nonnull PermissionType permissionType, boolean statusDefault) {
        if(findType(permissionType.getName()).isEmpty())
            throw new IllegalArgumentException("Given permission type " + permissionType.getName() + " was not registered yet!");

        return this.permissionEntityManager.create(guild,
                x -> {
                    x.setPermissionTypeName(permissionType.getName());
                    x.setStatusDefault(statusDefault);
                });
    }

    @Nonnull
    public Permission createPermission(@Nonnull Guild guild, @Nonnull PermissionType permissionType) {
        return createPermission(guild, permissionType, false);
    }

    // ===

    /**
     * Get the {@link PermissionRole} for the given Role or create it if it does not exist.
     * @param role the role
     * @return the permission role
     */
    @Nonnull
    public PermissionRole getRolePermissions(@Nonnull Role role) {
        return this.roleEntityManager.firstOrCreate(PermissionRole.filterRole(role),
                x -> x.create(DiscordPointer.to(role)));
    }

    /**
     * Get the {@link PermissionRole} for the public Role (@everyone) of the given guild or
     * create it if it does not exist.
     * @param guild the guild
     * @return the permission role
     * @see #getRolePermissions(Role)
     */
    @Nonnull
    public PermissionRole getRolePermissionsPublic(@Nonnull Guild guild) {
        return getRolePermissions(guild.getPublicRole());
    }

    /**
     * Get the {@link PermissionMember} for the given Member or create it if it does not exist.
     * @param member the member
     * @return the permission member
     */
    @Nonnull
    public PermissionMember getMemberPermissions(@Nonnull Member member) {
        return this.memberEntityManager.firstOrCreate(member);
    }

    // ===

    /**
     * Test if the given member has the given permission, either directly via {@link PermissionMember}
     * or due to their roles via {@link PermissionRole}.
     * @param member the member
     * @param permission the permission
     * @return true
     */
    public boolean test(@Nonnull Member member, @Nonnull Permission permission) {

        // 1. test directly

        PermissionContainer permissionsDirect = this.getMemberPermissions(member).getPermissions();

        if(permissionsDirect.contains(permission)) {
            return permissionsDirect.allows(permission);
        }

        // 2. test roles (highest -> lowest)

        for (Role role : member.getRoles()) {
            PermissionContainer permissionsRole = this.getRolePermissions(role).getPermissions();
            if(permissionsRole.contains(permission)) {
                return permissionsRole.allows(permission);
            }
        }

        // 3. test public role ("lowest")

        PermissionContainer permissionsPublicRole = this.getRolePermissionsPublic(member.getGuild()).getPermissions();
        if(permissionsPublicRole.contains(permission)) {
            return permissionsPublicRole.allows(permission);
        }

        // 4. use default since the permission was never set anywhere

        return permission.getStatusDefault();
    }

    // ===

    @Nonnull
    public static Permissions get() {
        return PermissionsModule.get().getPermissions();
    }
}
