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

package cynoodle.test.notifications;

import cynoodle.discord.DiscordPointer;
import cynoodle.entity.NestedEntity;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentArray;
import cynoodle.mongodb.fluent.FluentDocument;

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
