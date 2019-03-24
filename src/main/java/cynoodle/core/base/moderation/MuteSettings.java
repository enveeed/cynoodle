/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;

@EIdentifier("base:moderation:mute_settings")
public final class MuteSettings extends GEntity {
    private MuteSettings() {}

    // ===

    /**
     * The role which shall be used as the mute role.
     */
    private DiscordPointer role = null;

    /**
     * The default duration for mutes.
     */
    private Duration defaultDuration = Duration.ofMinutes(15);

    // ===

    @Nonnull
    public Optional<DiscordPointer> getRole() {
        return Optional.ofNullable(this.role);
    }

    public void setRole(@Nullable DiscordPointer role) {
        this.role = role;
    }

    @Nonnull
    public Optional<Duration> getDefaultDuration() {
        return Optional.ofNullable(this.defaultDuration);
    }

    public void setDefaultDuration(@Nullable Duration defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.role = source.getAt("role").asNullable(DiscordPointer.fromBson()).or(this.role);

        // TODO default duration etc.
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("role").asNullable(DiscordPointer.toBson()).to(this.role);

        // TODO default duration etc.

        return data;
    }
}
