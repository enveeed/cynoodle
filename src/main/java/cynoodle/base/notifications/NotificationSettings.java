/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
