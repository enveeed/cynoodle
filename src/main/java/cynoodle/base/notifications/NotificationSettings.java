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

import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentArray;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cynoodle.base.notifications.NotificationsModule.SUB_PROPERTIES;

/**
 * Settings for a Guilds notifications.
 */
@EIdentifier("base:notifications:settings")
public final class NotificationSettings extends GEntity {
    private NotificationSettings() {}

    // ===

    private Map<String, NotificationProperties> properties = new HashMap<>();

    // === DEFAULTS ===

    // TODO ...

    // ===

    @Nonnull
    public Set<NotificationProperties> getProperties() {
        return new HashSet<>(this.properties.values());
    }

    @Nonnull
    public NotificationProperties getOrCreateProperties(@Nonnull String identifier) {
        if(this.properties.containsKey(identifier)) return this.properties.get(identifier);
        else {
            NotificationProperties properties = SUB_PROPERTIES.create(this);
            properties.create(identifier);

            this.properties.put(identifier, properties);

            this.persist();

            return properties;
        }
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.properties = source.getAt("properties").asArray().or(FluentArray.wrapNew())
                .collect().as(SUB_PROPERTIES.load(this))
                .toMap(NotificationProperties::getIdentifier);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("properties").asArray().to(FluentArray.wrapNew()
        .insert().as(SUB_PROPERTIES.store())
                .atEnd(this.properties.values()));

        return data;
    }


}
