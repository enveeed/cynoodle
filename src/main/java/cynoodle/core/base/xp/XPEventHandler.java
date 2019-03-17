/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.eventbus.Subscribe;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.events.EventListener;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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
        DiscordPointer message = DiscordPointer.to(event.getMessage());

        // === GAIN ===

        module.controller()
                .onMember(guild, user)
                .gain(message);

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
