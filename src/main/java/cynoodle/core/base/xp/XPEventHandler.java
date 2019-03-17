/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
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

        // === GAIN ===

        module.controller()
                .onMember(DiscordPointer.to(event.getGuild()), DiscordPointer.to(event.getAuthor()))
                .gain(DiscordPointer.to(event.getMessage()));

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
