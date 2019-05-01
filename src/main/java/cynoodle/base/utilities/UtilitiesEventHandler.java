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

package cynoodle.base.utilities;

import cynoodle.base.notifications.NotificationsModule;
import cynoodle.discord.DiscordEvent;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.events.EventListener;
import cynoodle.module.Module;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;

import javax.annotation.Nonnull;

public final class UtilitiesEventHandler implements EventListener {
    UtilitiesEventHandler() {}

    // ===

    void onEvent(@Nonnull DiscordEvent event) {
        if(event.is(GuildMemberJoinEvent.class))
            onGuildMemberJoin(event.get(GuildMemberJoinEvent.class));
        if(event.is(GuildMemberLeaveEvent.class))
            onGuildMemberLeave(event.get(GuildMemberLeaveEvent.class));
    }

    // ===

    private void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getUser());

        Module.get(NotificationsModule.class).controller().onGuild(guild).emit("base:utilities:member_join",
                Members.formatAt(guild).format(user));

    }

    private void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getUser());

        Module.get(NotificationsModule.class).controller().onGuild(guild).emit("base:utilities:member_leave",
                Members.formatAt(guild).format(user));

    }
}
