/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.Random;
import cynoodle.core.base.notifications.NotificationsModule;
import cynoodle.core.discord.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class XPController {

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final XPModule module = Module.get(XPModule.class);

    private final MEntityManager<XP> xpManager
            = module.getXPManager();
    private final RankManager rankManager
            = module.getRankManager();
    private final GEntityManager<XPSettings> settingsManager
            = module.getSettingsManager();
    private final XPStatusManager statusManager
            = module.getXPStatusManager();

    private final NotificationsModule notificationsModule = Module.get(NotificationsModule.class);

    // ===

    protected XPController() {}

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

    public final class OnGuild {

        private final DiscordPointer guild;

        // ===

        private OnGuild(@Nonnull DiscordPointer guild) {
            this.guild = guild;
        }

        // ===

    }

    public final class OnMember {

        private final DiscordPointer guild;
        private final DiscordPointer user;

        // ===

        private OnMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
            this.guild = guild;
            this.user = user;
        }

        // ===

        public long get() {

            XP xp = module.getXPManager().firstOrCreate(guild, user);

            return xp.get();
        }

        // ===

        public void gain(@Nullable DiscordPointer context) {

            XPSettings settings     = settingsManager.firstOrCreate(this.guild);
            XPStatus status         = statusManager.get(this.guild);

            //

            long value = 0L;

            // === GAIN ===

            if(!status.isInTimeout(user)) {
                status.updateLastGain(user);

                long gain = Random.nextLong(settings.getGainMin(), settings.getGainMax());

                value = value + gain;
            }

            // === DROP ===

            // TODO not just one XP bomb ...

            if(settings.isDropsEnabled()) {

                int chance = Random.nextInt(0, 999);

                if(chance == 0) {

                    long gain = Random.nextLong(2442L, 6556L);

                    value = value + gain;

                    Module.get(NotificationsModule.class)
                            .controller().onGuild(guild)
                            .emit(XPModule.NOTIFICATION_XP_BOMB.create(context,
                                    Members.formatAt(guild).format(user), Long.toString(gain)));
                }
            }

            // === FINALIZE ===

            if(value == 0L) return; // ignore if nothing was added

            modify(value, context);
        }

        // ===

        public void modify(long change, @Nullable DiscordPointer context) {

            XP xp = module.getXPManager().firstOrCreate(guild, user);

            long previous;

            if(change > 0) {
                previous = xp.add(change);
            }
            else if(change < 0) {
                previous = xp.remove(-change);
            }
            else return;

            xp.persist();

            //

            long current = xp.get();

            LOG.atFiner().log("Updated XP for %s:%s %s %s %s -> %s", guild, user, previous,
                    change >= 0 ? "+" : "-", change >= 0 ? change : -change, current);

            this.handleModification(previous, current, context);
        }

        public void modify(long change) {
            this.modify(change, null);
        }

        //

        private void handleModification(long previous, long current, @Nullable DiscordPointer context) {

            XPFormula formula = module.getFormula();

            int levelPrevious = formula.getReachedLevel(previous);
            int levelCurrent = formula.getReachedLevel(current);

            if(levelPrevious != levelCurrent) {

                LOG.atFiner().log("Level change detected for %s:%s %s -> %s", guild, user, levelPrevious, levelCurrent);

                this.applyRanks();

                if(levelPrevious < levelCurrent) {
                    notificationsModule.controller()
                            .onGuild(this.guild)
                            .emit(XPModule.NOTIFICATION_LEVEL_UP
                                    .create(context, Members.formatAt(guild).format(user), String.valueOf(levelCurrent)));
                } else {
                    notificationsModule.controller()
                            .onGuild(this.guild)
                            .emit(XPModule.NOTIFICATION_LEVEL_DOWN
                                    .create(context, Members.formatAt(guild).format(user), String.valueOf(levelCurrent)));
                }

                // TODO rank notifications
            }
        }

        // ===

        /**
         * Apply the Ranks for this Member.
         * This will add all roles from the effective Rank of the level of the member
         * and all roles from all previous Ranks which are set to 'keep'.
         */
        public void applyRanks() {

            XP xp = module.getXPManager().firstOrCreate(guild, user);
            XPFormula formula = module.getFormula();

            Guild guild = xp.requireGuild().asGuild()
                    .orElseThrow(() -> new IllegalStateException("No Guild!"));

            Member member = xp.requireUser().asMember(guild)
                    .orElseThrow(() -> new IllegalStateException("No Member!"));

            //

            int level = formula.getReachedLevel(xp.get());

            //

            List<Rank> allRanks = rankManager.list(Rank.filterGuild(xp.requireGuild()));

            List<Rank> previousAndThisRanks = allRanks.stream()
                    .sorted()
                    .filter(rank -> rank.getLevel() <= level).collect(Collectors.toList());

            //

            List<Role> allRoles = new ArrayList<>();

            for (Rank rank : allRanks) {
                for (Rank.Role role : rank.getRoles()) {

                    Role actualRole = guild.getRoleById(role.getRole().getID());

                    if(actualRole == null) {

                        // TODO warn that there was an unknown role

                        continue;
                    }

                    allRoles.add(actualRole);
                }
            }

            List<Role> effectiveRoles = new ArrayList<>();

            for (int i = 0; i < previousAndThisRanks.size(); i++) {
                Rank rank = previousAndThisRanks.get(i);

                if(rank.getRoles().size() == 0) continue;

                for (Rank.Role role : rank.getRoles()) {
                    // non-keep roles would only apply if this rank is the effective one right now,
                    // which means its the last one
                    if(!role.isKeepEnabled() && i != previousAndThisRanks.size() - 1) continue;

                    DiscordPointer pointer = role.getRole();

                    Role actualRole = guild.getRoleById(pointer.getID());

                    if(actualRole == null) {

                        // TODO warn that there was an unknown role

                        continue;
                    }

                    effectiveRoles.add(actualRole);
                }
            }

            //

            RModifier modifier = RModifier.on(member);

            modifier.remove(allRoles);
            modifier.add(effectiveRoles);

            //

            modifier.done().reason("Applied Ranks").queue();
        }
    }
}
