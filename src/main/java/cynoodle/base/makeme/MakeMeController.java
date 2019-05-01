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

package cynoodle.base.makeme;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mongodb.client.model.Filters;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.discord.RModifier;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public OnGuild onGuild(@Nonnull DiscordPointer guild) {
        return new OnGuild(guild);
    }

    @Nonnull
    public OnMember onMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return new OnMember(guild, user);
    }

    // ===

    /**
     * API for a guilds {@link MakeMe}.
     */
    public final class OnGuild {

        private final DiscordPointer guild;

        // ===

        private OnGuild(@Nonnull DiscordPointer guild) {
            this.guild = guild;
        }

        // ===

        @Nonnull
        @CanIgnoreReturnValue
        public MakeMe create(@Nonnull String key, @Nonnull String name, @Nonnull DiscordPointer role) {

            boolean existing = makeMeManager
                    .exists(Filters.and(MakeMe.filterGuild(guild), MakeMe.filterKey(key)));

            if(existing) throw new IllegalArgumentException("There is already a make-me with this key: \"" + key + "\"!");

            //

            return makeMeManager.create(this.guild, mm -> mm.create(key, name, role));
        }

        @Nonnull
        @CanIgnoreReturnValue
        public MakeMeGroup createGroup(@Nonnull String key, @Nonnull String name) {

            boolean existingGroup = groupManager
                    .exists(Filters.and(MakeMeGroup.filterGuild(guild), MakeMeGroup.filterKey(key)));
            boolean existingMakeMe = makeMeManager
                    .exists(Filters.and(MakeMe.filterGuild(guild), MakeMe.filterKey(key)));

            if(existingGroup || existingMakeMe)
                throw new IllegalArgumentException("There is already a make-me group or make-me with this key: \"" + key + "\"!");

            //

            return groupManager.create(this.guild, group -> group.create(key, name));
        }

        //

        @Nonnull
        public Optional<MakeMe> find(@Nonnull String key) {
            return makeMeManager.first(Filters.and(MakeMe.filterGuild(this.guild), MakeMe.filterKey(key)));
        }

        @Nonnull
        public Optional<MakeMeGroup> findGroup(@Nonnull String key) {
            return groupManager.first(Filters.and(MakeMeGroup.filterGuild(this.guild), MakeMeGroup.filterKey(key)));
        }

        //

        @Nonnull
        public Stream<MakeMe> allByGroup(@Nullable MakeMeGroup group) {
            if(group == null) return makeMeManager.stream(this.guild, MakeMe.filterGroup(null));
            else return makeMeManager.stream(this.guild, MakeMe.filterGroup(group));
        }

        //

        @Nonnull
        public Stream<MakeMe> all() {
            return makeMeManager.stream(this.guild);
        }

        @Nonnull
        public Stream<MakeMeGroup> allGroups() {
            return groupManager.stream(this.guild);
        }

    }

    /**
     * API for a single members {@link MakeMe}.
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

            //

            this.apply();
        }

        //

        public void unmake(@Nonnull MakeMe mm) {

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            if(!status.has(mm)) return;

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

        public boolean has(@Nonnull MakeMe makeme) {

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            return status.has(makeme);
        }

        //

        public void apply() {

            Guild guild = this.guild.asGuild()
                    .orElseThrow();
            Member member = this.user.asMember(guild)
                    .orElseThrow();

            //

            MakeMeStatus status = statusManager.firstOrCreate(this.guild, this.user);

            Set<MakeMe> applying = status.get();
            Set<MakeMe> all = makeMeManager.stream(this.guild).collect(Collectors.toSet());

            Set<Role> applyingRoles = new HashSet<>();
            Set<Role> allRoles = new HashSet<>();

            //

            for (MakeMe mm : all) {

                if(!mm.canAccess(user)) {
                    // TODO remove it from storage as well
                    continue;
                }

                Optional<Role> roleResult = mm.getRole().asRole();
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

            action.reason("Applied make-me")
                    .queue();
        }

    }
}
