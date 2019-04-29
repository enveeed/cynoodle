/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp;

import com.google.common.eventbus.Subscribe;
import cynoodle.discord.DiscordEvent;
import cynoodle.discord.DiscordPointer;
import cynoodle.events.EventListener;
import cynoodle.module.Module;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

final class XPEventHandler implements EventListener {
    XPEventHandler() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    private void onEvent(@Nonnull DiscordEvent event) {
        if(event.is(GuildMessageReceivedEvent.class))
            onMessage(event.get(GuildMessageReceivedEvent.class));
        else if(event.is(GuildMemberJoinEvent.class))
            onJoin(event.get(GuildMemberJoinEvent.class));
    }

    // ===

    private void onMessage(@Nonnull GuildMessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return; // ignore bot


        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getAuthor());
        DiscordPointer channel = DiscordPointer.to(event.getChannel());

        // === GAIN ===

        module.controller()
                .onMember(guild, user)
                .gain(channel);

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
