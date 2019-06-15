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

import com.google.common.eventbus.Subscribe;
import cynoodle.base.commands.CommandRegistry;
import cynoodle.base.commands.CommandsModule;
import cynoodle.base.notifications.NotificationType;
import cynoodle.base.notifications.NotificationTypeRegistry;
import cynoodle.base.notifications.NotificationsModule;
import cynoodle.discord.DiscordEvent;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

/**
 * Contains utility commands, notifications, etc.
 */
@MIdentifier("base:utilities")
@MRequires("base:commands")
@MRequires("base:notifications")
public final class UtilitiesModule extends Module {
    private UtilitiesModule() {}

    private static final NotificationType NOTIFICATION_JOIN = NotificationType.of("base:utilities:member_join",
            "member");
    private static final NotificationType NOTIFICATION_LEAVE = NotificationType.of("base:utilities:member_leave",
            "member");

    UtilitiesEventHandler handler = new UtilitiesEventHandler();

    // ===

    @Override
    protected void start() {
        super.start();

        CommandRegistry commandRegistry = Module.get(CommandsModule.class).getRegistry();

        commandRegistry.register(VersionCommand.class);
        commandRegistry.register(ChooseOfRoleCommand.class);
        commandRegistry.register(TemporaryHelpCommand.class);
        commandRegistry.register(TemporarySetupCommand.class);

        NotificationTypeRegistry notificationTypeRegistry = Module.get(NotificationsModule.class).getRegistry();

        notificationTypeRegistry.register(NOTIFICATION_JOIN);
        notificationTypeRegistry.register(NOTIFICATION_LEAVE);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    public UtilitiesEventHandler getHandler() {
        return handler;
    }

    // ===

    @Subscribe
    private void onEvent(DiscordEvent de) {
        handler.onEvent(de);
    }
}
