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

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public final class PermissionsEventHandler extends ListenerAdapter {
    PermissionsEventHandler() {}

    // ===

    // delete member permissions when a member leaves (and is no longer a member)
    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        Permissions permissions = Permissions.get();

        Member member = event.getMember();

        permissions.getMemberEntityManager()
                .deleteAll(PermissionMember.filterMember(member));
    }

    // delete role permissions when a role is deleted
    @Override
    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
        Permissions permissions = Permissions.get();

        Role role = event.getRole();

        permissions.getRoleEntityManager()
                .deleteAll(PermissionRole.filterRole(role));

    }
}
