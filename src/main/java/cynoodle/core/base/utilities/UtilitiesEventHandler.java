/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.utilities;

import cynoodle.core.base.notifications.NotificationController;
import cynoodle.core.base.notifications.NotificationsModule;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.events.EventListener;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;

import javax.annotation.Nonnull;

public final class UtilitiesEventHandler implements EventListener {
    UtilitiesEventHandler() {}

    private final NotificationController controller = Module.get(NotificationsModule.class).controller();

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

        controller.onGuild(guild).emit("base:utilities:member_join",
                Members.formatAt(guild).format(user));

    }

    private void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getUser());

        controller.onGuild(guild).emit("base:utilities:member_leave",
                Members.formatAt(guild).format(user));

    }
}
