/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.mm;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.discord.RModifier;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fluent controller for {@link MakeMe} assignment.
 */
public final class MakeMeController {
    MakeMeController() {}

    private final MakeMeModule module = Module.get(MakeMeModule.class);

    private final GEntityManager<MakeMe> makeMeManager
            = module.getMakeMeManager();
    private final GEntityManager<MakeMeGroup> groupManager
            = module.getGroupManager();
    private final MEntityManager<MakeMeStatus> statusManager
            = module.getStatusManager();

    // ===

    @Nonnull
    public OnMember onMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return new OnMember(guild, user);
    }

    // ===

    /**
     * API for a single members {@link MakeMe} status.
     */
    public final class OnMember {

        private final DiscordPointer guild;
        private final DiscordPointer user;

        // ===

        private OnMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
            this.guild = guild;
            this.user = user;
        }

        // ===

        public void make(@Nonnull MakeMe mm) {

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            Optional<MakeMeGroup> groupResult = mm.getGroup();
            if(groupResult.isPresent()) {

                MakeMeGroup group = groupResult.orElseThrow();

                if(group.isUniqueEnabled()) {

                    // remove all MMs in this group from the member
                    makeMeManager
                            .stream(this.guild, MakeMe.filterGroup(group))
                            .forEach(status::remove);
                }
            }

            status.add(mm);
            status.persist();

            this.apply();
        }

        //

        public void unmake(@Nonnull MakeMe mm) throws IllegalStateException {

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            if(!status.has(mm))
                throw new IllegalStateException("User " + user + " does not have MakeMe " + mm + ".");

            status.remove(mm);
            status.persist();

            this.apply();
        }

        public void unmake(@Nonnull MakeMeGroup group) {

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            makeMeManager
                    .stream(this.guild, MakeMe.filterGroup(group))
                    .forEach(status::remove);

            status.persist();

            this.apply();
        }

        //

        public void apply() {

            Guild guild = this.guild.asGuild()
                    .orElseThrow();
            Member member = this.guild.asMember(guild)
                    .orElseThrow();

            //

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            Set<MakeMe> applying = status.get();
            Set<MakeMe> all = makeMeManager.stream(this.guild).collect(Collectors.toSet());

            Set<Role> applyingRoles = new HashSet<>();
            Set<Role> allRoles = new HashSet<>();

            //

            for (MakeMe mm : all) {

                Optional<Role> roleResult = mm.getRole().asRole(guild);
                if(roleResult.isEmpty()) {
                    // TODO warn non-existing role
                    continue;
                }

                Role role = roleResult.orElseThrow();

                if(applying.contains(mm)) applyingRoles.add(role);
                allRoles.add(role);
            }

            //

            AuditableRestAction<Void> action = RModifier.on(member)
                    .remove(allRoles)
                    .add(applyingRoles)
                    .done();

            action.reason("Applied MakeMe")
                    .queue();
        }

    }
}
