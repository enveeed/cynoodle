/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Random;
import cynoodle.core.base.notifications.NotificationsModule;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

/**
 * Handling of events for the XP module.
 */
final class XPEventHandler {

    private final XPModule module;

    //

    XPEventHandler(@Nonnull XPModule module) {
        this.module = module;
    }

    //

    void onEvent(@Nonnull DiscordEvent event) {
        if(event.is(GuildMessageReceivedEvent.class))
            onMessage(event.get(GuildMessageReceivedEvent.class));
        else if(event.is(GuildMemberJoinEvent.class))
            onJoin(event.get(GuildMemberJoinEvent.class));
    }

    //

    private void onMessage(@Nonnull GuildMessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return; // ignore bot

        //

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getAuthor());

        //

        XPSettings settings     = this.module.getSettingsManager().firstOrCreate(event.getGuild());
        XPStatus status         = this.module.status.computeIfAbsent(guild, XPStatus::new);

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
                        .emit(XPModule.NOTIFICATION_XP_BOMB.create(DiscordPointer.to(event.getChannel()),
                                Members.formatAt(guild).format(user), Long.toString(gain)));
            }
        }

        // === FINALIZE ===

        if(value == 0L) return; // ignore if nothing was added

        module.controller()
                .onMember(guild, user)
                .modify(value, DiscordPointer.to(event.getChannel()));

    }

    //

    private void onJoin(@Nonnull GuildMemberJoinEvent event) {

        if(event.getUser().isBot()) return; // ignore bot

        //

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getUser());

        // === RANKS ===

        module.controller()
                .onMember(guild, user)
                .applyRanks();
    }

}
