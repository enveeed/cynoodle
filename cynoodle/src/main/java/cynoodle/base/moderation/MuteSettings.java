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

package cynoodle.base.moderation;

import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;

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
