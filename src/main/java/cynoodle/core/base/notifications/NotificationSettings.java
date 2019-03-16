/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Settings for a Guilds notifications.
 */
@EIdentifier("base:notifications:settings")
public final class NotificationSettings extends GEntity {
    private NotificationSettings() {}

    // === DEFAULTS ===

    /**
     * The default channel for notifications.
     */
    private DiscordPointer defaultChannel = null;

    /**
     * The default setting for if the context, if existent
     * should be preferred to the defined channel.
     */
    private boolean defaultContextPreferred = true;

    /**
     * The default setting for if notifications should
     * be discarded if there is no context.
     * (false if contextPreferred false)
     */
    private boolean defaultContextOnly = true;

    // ===

    @Nonnull
    public Optional<DiscordPointer> getChannelDefault() {
        return Optional.ofNullable(this.defaultChannel);
    }

    public boolean isContextPreferredEnabledDefault() {
        return this.defaultContextPreferred;
    }

    public boolean isContextOnlyEnabledDefault() {
        return this.defaultContextOnly;
    }

    // ===

    @Nonnull
    public Optional<DiscordPointer> getChannel(@Nonnull NotificationType type) {
        return getChannelDefault(); // TODO replace with override and effective properties class
    }

    public boolean isContextPreferredEnabled(@Nonnull NotificationType type) {
        return isContextPreferredEnabledDefault(); // TODO replace with override and effective properties class
    }

    public boolean isContextOnly(@Nonnull NotificationType type) {
        if(!isContextPreferredEnabled(type)) return false;
        else return isContextOnlyEnabledDefault(); // TODO replace with override and effective properties class
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.defaultChannel = source.getAt("default_channel").asNullable(DiscordPointer.fromBson()).or(this.defaultChannel);
        this.defaultContextPreferred = source.getAt("default_context_preferred").asBoolean().or(this.defaultContextPreferred);
        this.defaultContextOnly = source.getAt("default_context_only").asBoolean().or(this.defaultContextOnly);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("default_channel").asNullable(DiscordPointer.toBson()).to(this.defaultChannel);
        data.setAt("default_context_preferred").asBoolean().to(this.defaultContextPreferred);
        data.setAt("default_context_only").asBoolean().to(this.defaultContextOnly);

        return data;
    }
}
