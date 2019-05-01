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

package cynoodle.discord;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.GuildController;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public final class RModifier {

    private Member member;

    private List<Role> roles = Lists.newArrayList();
    private List<Role> current = Lists.newArrayList();

    // ===

    private RModifier(@Nonnull Member member) {
        this.member = member;
        this.roles.addAll(member.getRoles());
        this.current.addAll(member.getRoles());
    }

    // ===

    @Nonnull
    public RModifier add(@Nonnull Role... roles) {
        this.roles.addAll(Lists.newArrayList(roles));
        return this;
    }

    @Nonnull
    public RModifier add(@Nonnull Collection<Role> roles) {
        this.roles.addAll(roles);
        return this;
    }

    @Nonnull
    public RModifier remove(@Nonnull Role... roles) {
        this.roles.removeAll(Lists.newArrayList(roles));
        return this;
    }

    @Nonnull
    public RModifier remove(@Nonnull Collection<Role> roles) {
        this.roles.removeAll(roles);
        return this;
    }

    @Nonnull
    public RModifier clear() {
        this.roles.clear();
        return this;
    }

    // ===

    @CheckReturnValue
    @Nonnull
    public AuditableRestAction<Void> done() {

        GuildController controller = member.getGuild().getController();

        List<Role> roles_remove = Lists.newArrayList();
        List<Role> roles_add = Lists.newArrayList();

        for (Role role : current) {
            if(!roles.contains(role)) roles_remove.add(role);
        }

        for(Role role: roles){
            if(!current.contains(role)) roles_add.add(role);
        }

        return controller.modifyMemberRoles(member, roles_add, roles_remove);
    }

    // ===

    @Nonnull
    public static RModifier on(@Nonnull Member member) {
        return new RModifier(member);
    }

}
