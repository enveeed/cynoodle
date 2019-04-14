/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

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
