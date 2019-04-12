/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.entities.NestedEntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:notifications")
public final class NotificationsModule extends Module {
    private NotificationsModule() {}

    final static EntityType<NotificationSettings> ENTITY_SETTINGS = EntityType.of(NotificationSettings.class);
    final static NestedEntityType<NotificationProperties> SUB_PROPERTIES = NestedEntityType.of(NotificationProperties.class);

    //

    private GEntityManager<NotificationSettings> settingsManager;

    //

    private NotificationTypeRegistry registry;

    //

    private NotificationController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        this.settingsManager = new GEntityManager<>(ENTITY_SETTINGS);

        //

        this.registry = new NotificationTypeRegistry();

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
    public NotificationTypeRegistry getRegistry() {
        return this.registry;
    }

    //

    @Nonnull
    public NotificationController controller() {
        return this.controller;
    }
}
