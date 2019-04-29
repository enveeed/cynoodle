/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
