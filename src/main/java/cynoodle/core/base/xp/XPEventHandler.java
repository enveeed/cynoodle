/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Random;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

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
        DiscordPointer member = DiscordPointer.to(event.getAuthor());

        //

        XPSettings settings = this.module.getSettingsManager().firstOrCreate(event.getGuild());
        XPStatus status = module.status.computeIfAbsent(guild, XPStatus::new);
        XP xp = module.getXPManager().firstOrCreate(guild, member);

        //

        long value = 0L;

        // === GAIN ===

        if(!status.isInTimeout(member)) {
            status.updateLastGain(member);

            long gain = Random.nextLong(settings.getGainMin(), settings.getGainMax());

            value = value + gain;
        }

        // === DROP ===

        if(settings.isDropsEnabled()) {

            int chance = Random.nextInt(0, 999);

            if(chance == 0) {

                long gain = Random.nextLong(2442L, 6556L);

                value = value + gain;
            }
        }

        // === FINALIZE ===

        if(value == 0L) return; // ignore if nothing was added

        xp.add(value);
        xp.persist();

    }

    //

    private void onJoin(@Nonnull GuildMemberJoinEvent event) {

        if(event.getUser().isBot()) return; // ignore bot

        //

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer member = DiscordPointer.to(event.getUser());

        //

        Optional<XP> xpResult = module.getXPManager().first(XP.filterMember(guild, member));
        if(xpResult.isEmpty()) return; // ignore

        XP xp = xpResult.orElseThrow();

        // === RANKS ===

        xp.applyRanks();
    }

}
