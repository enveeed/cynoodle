/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.utilities;

import com.google.common.eventbus.Subscribe;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.notifications.NotificationType;
import cynoodle.core.base.notifications.NotificationTypeRegistry;
import cynoodle.core.base.notifications.NotificationsModule;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

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
