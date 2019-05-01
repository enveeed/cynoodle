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
