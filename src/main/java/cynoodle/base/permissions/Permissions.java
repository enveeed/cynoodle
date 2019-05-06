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

import com.google.common.flogger.FluentLogger;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.entities.EntityManager;
import cynoodle.entities.EntityReference;
import cynoodle.mongo.fluent.Codec;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central permission management.
 * Obtain instance via {@link #get()}, requires {@link PermissionsModule} to be active.
 */
public final class Permissions {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    Permissions() {}

    // ===

    private final GEntityManager<Permission> permissionEntityManager
            = new GEntityManager<>(Permission.TYPE);
    private final EntityManager<PermissionRole> roleEntityManager
            = new EntityManager<>(PermissionRole.TYPE);
    private final MEntityManager<PermissionMember> memberEntityManager
            = new MEntityManager<>(PermissionMember.TYPE);

    // ===

    GEntityManager<Permission> getPermissionEntityManager() {
        return this.permissionEntityManager;
    }

    EntityManager<PermissionRole> getRoleEntityManager() {
        return this.roleEntityManager;
    }

    MEntityManager<PermissionMember> getMemberEntityManager() {
        return this.memberEntityManager;
    }

    // ===

    @Nonnull
    public Optional<Permission> getPermission(long id) {
        return this.permissionEntityManager.get(id);
    }

    //

    @Nonnull
    public Permission createPermission(@Nonnull Guild guild,
                                       @Nonnull String name,
                                       boolean statusDefault) {
        return this.permissionEntityManager.create(guild,
                x -> {
                    x.setName(name);
                    x.setStatusDefault(statusDefault);
                });
    }

    @Nonnull
    public Permission createPermission(@Nonnull Guild guild,
                                       @Nonnull String name) {
        return this.permissionEntityManager.create(guild,
                x -> {
                    x.setName(name);
                });
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
     * or due to their roles via {@link PermissionRole}. If this is not the case, use the default status of the permission.
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

    /**
     * Get a set of all permissions known on the given Guild.
     * NOTE: This method also deletes any permissions encountered which have an unknown type.
     * @param guild the guild
     * @return a set of all permissions for this guild
     */
    @Nonnull
    public Set<Permission> getPermissions(@Nonnull Guild guild) {
        return this.permissionEntityManager.stream(guild).collect(Collectors.toUnmodifiableSet());
    }

    // ===

    // TODO this is ugly, find another way to do this (fix entity ref. as a whole)
    @Nonnull
    public Codec<EntityReference<Permission>> codecPermissionReference() {
        return this.permissionEntityManager.referenceCodec();
    }

    // ===

    @Nonnull
    public static Permissions get() {
        return PermissionsModule.get().getPermissions();
    }
}
