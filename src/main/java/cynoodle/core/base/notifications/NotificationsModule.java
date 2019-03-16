/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:notifications")
public final class NotificationsModule extends Module {
    private NotificationsModule() {}

    private final static EntityType<NotificationSettings> TYPE_SETTINGS = EntityType.of(NotificationSettings.class);

    //

    private GEntityManager<NotificationSettings> settingsManager;

    //

    private NotificationController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        this.settingsManager = new GEntityManager<>(TYPE_SETTINGS);

        //

        this.controller = new NotificationController();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public GEntityManager<NotificationSettings> getSettingsManager() {
        return this.settingsManager;
    }

    //

    @Nonnull
    public NotificationController controller() {
        return this.controller;
    }
}
