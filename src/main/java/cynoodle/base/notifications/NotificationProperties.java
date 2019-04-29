/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.notifications;

import cynoodle.discord.DiscordPointer;
import cynoodle.entities.NestedEntity;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentArray;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Properties for a notification type, within {@link NotificationSettings}.
 */
public final class NotificationProperties extends NestedEntity {
    private NotificationProperties() {}

    /**
     * The identifier of the notification type these properties are for.
     */
    private String identifier;

    /**
     * If this notification is enabled or not.
     */
    private boolean enabled = true;

    /**
     * The channel.
     */
    private DiscordPointer channel = null;

    /**
     * The messages.
     */
    private Set<String> messages = new HashSet<>();

    // == CONTEXT SETTINGS ==

    // TODO ...

    // ===

    void create(@Nonnull String identifier) {
        this.identifier = identifier;
    }

    // ===

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    //

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //

    @Nonnull
    public Optional<DiscordPointer> getChannel() {
        return Optional.ofNullable(this.channel);
    }

    public void setChannel(@Nullable DiscordPointer channel) {
        this.channel = channel;
    }

    //

    @Nonnull
    public Set<String> getMessages() {
        return this.messages;
    }

    public void setMessages(@Nonnull Set<String> messages) {
        this.messages = messages;
    }

    // ===

    // TODO ...

    // === DATA ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

        this.identifier = data.getAt("identifier").asString().value();

        this.enabled = data.getAt("enabled").asBoolean().or(this.enabled);

        this.channel = data.getAt("channel").asNullable(DiscordPointer.fromBson()).or(this.channel);
        this.messages = data.getAt("messages").asArray().or(FluentArray.wrapNew())
                .collect().asString().toSet();
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("identifier").asString().to(this.identifier);

        data.setAt("enabled").asBoolean().to(this.enabled);

        data.setAt("channel").asNullable(DiscordPointer.toBson()).to(this.channel);
        data.setAt("messages").asArray().to(FluentArray.wrapNew()
                .insert().asString().atEnd(this.messages));

        return data;
    }

    // TODo test only


    @Override
    public String toString() {
        return "NotificationProperties{" +
                "identifier='" + identifier + '\'' +
                ", enabled=" + enabled +
                ", channel=" + channel +
                ", messages=" + messages +
                '}';
    }
}
