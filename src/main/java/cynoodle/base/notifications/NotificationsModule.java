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

package cynoodle.base.notifications;

import cynoodle.discord.GEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.entities.NestedEntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.Module;

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
