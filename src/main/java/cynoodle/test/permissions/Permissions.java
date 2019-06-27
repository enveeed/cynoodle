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

package cynoodle.test.permissions;

import com.google.common.flogger.FluentLogger;
import cynoodle.discord.MEntityManager;
import cynoodle.entity.EntityManager;
import cynoodle.discord.DiscordPointer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Central permission management.
 * Obtain instance via {@link #get()}, requires {@link PermissionsModule} to be active.
 */
public final class Permissions {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    Permissions() {}

    // ===

    private final EntityManager<PermissionRole> roleEntityManager
            = new EntityManager<>(PermissionRole.TYPE);
    private final MEntityManager<PermissionMember> memberEntityManager
            = new MEntityManager<>(PermissionMember.TYPE);

    // ===

    EntityManager<PermissionRole> getRoleEntityManager() {
        return this.roleEntityManager;
    }

    MEntityManager<PermissionMember> getMemberEntityManager() {
        return this.memberEntityManager;
    }

    // === CONTAINERS ===

    /**
     * Get the {@link PermissionRole} for the given Role or create it if it does not exist.
     * @param role the role
     * @return the permission role
     */
    @Nonnull
    public PermissionRole forRole(@Nonnull Role role) {
        return this.roleEntityManager.firstOrCreate(PermissionRole.filterRole(role),
                x -> {
                    x.setRole(DiscordPointer.to(role));
                });
    }

    /**
     * Get the {@link PermissionRole} for the public Role (@everyone) of the given guild or
     * create it if it does not exist.
     * @param guild the guild
     * @return the permission role
     * @see #forRole(Role)
     */
    @Nonnull
    public PermissionRole forPublicRole(@Nonnull Guild guild) {
        return forRole(guild.getPublicRole());
    }

    /**
     * Get the {@link PermissionMember} for the given Member or create it if it does not exist.
     * @param member the member
     * @return the permission member
     */
    @Nonnull
    public PermissionMember forMember(@Nonnull Member member) {
        return this.memberEntityManager.firstOrCreate(member);
    }

    // === TEST ===

    /**
     * Test if the given member has the given permission, either because they are owner,
     * directly via {@link PermissionMember} or due to their roles via {@link PermissionRole}.
     * If this is not the case, use the fallback value.
     * @param member the member
     * @param permission the permission
     * @param fallback the fallback value
     * @return true if the member has the permission, false if denied, the fallback otherwise
     */
    public boolean test(@Nonnull Member member, @Nonnull Permission permission, boolean fallback) {

        // 0. test ownership override
        if(member.isOwner()) return true;

        // 1. test directly

        PermissionContainer permissionsDirect = this.forMember(member).getPermissions();

        if(permissionsDirect.contains(permission)) {
            return permissionsDirect.allows(permission);
        }

        // 2. test roles (highest -> lowest)

        for (Role role : member.getRoles()) {
            PermissionContainer permissionsRole = this.forRole(role).getPermissions();
            if(permissionsRole.contains(permission)) {
                return permissionsRole.allows(permission);
            }
        }

        // 3. test public role ("lowest")

        PermissionContainer permissionsPublicRole = this.forPublicRole(member.getGuild()).getPermissions();
        if(permissionsPublicRole.contains(permission)) {
            return permissionsPublicRole.allows(permission);
        }

        // 4. use fallback since the permission was never set anywhere

        return fallback;
    }

    public boolean test(@Nonnull Member member, @Nonnull Permission permission) {
        return test(member, permission, false);
    }

    // ===

    @Nonnull
    public static Permissions get() {
        return PermissionsModule.get().getPermissions();
    }
}
