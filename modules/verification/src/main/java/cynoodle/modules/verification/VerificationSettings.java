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

package cynoodle.modules.verification;

import cynoodle.discord.GEntity;
import cynoodle.discord.RReference;
import cynoodle.discord.TCReference;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EVersion;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalLong;

@EIdentifier(VerificationModule.IDENTIFIER + ":settings")
@EVersion(0)
public final class VerificationSettings extends GEntity {
    private VerificationSettings() {}

    static final EntityType<VerificationSettings> TYPE = EntityType.of(VerificationSettings.class);

    // ===

    /**
     * The role which all unverified members have.
     */
    @Nullable
    private RReference role = null;

    /**
     * The channel in which the {@link #message} resides.
     */
    @Nullable
    private TCReference channel = null;

    /**
     * The message (in the {@link #channel}) which contains the reaction that should be used for verification.
     * (-1 = not set / null)
     */
    private long message = -1L;

    /**
     * The reaction that needs to be added to verify a user.
     */
    @Nullable
    private String reaction = null;

    // ===

    @Nonnull
    public Optional<RReference> getRole() {
        return Optional.ofNullable(role);
    }

    public void setRole(@Nullable RReference role) {
        this.role = role;
    }

    @Nonnull
    public Optional<TCReference> getChannel() {
        return Optional.ofNullable(channel);
    }

    public void setChannel(@Nullable TCReference channel) {
        this.channel = channel;
    }

    @Nonnull
    public OptionalLong getMessage() {
        return this.message == -1 ? OptionalLong.empty() : OptionalLong.of(message);
    }

    public void setMessage(long message) {
        this.message = message < 0 ? -1L : message;
    }

    @Nonnull
    public Optional<String> getReaction() {
        return Optional.ofNullable(reaction);
    }

    public void setReaction(@Nullable String reaction) {
        this.reaction = reaction;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.role = source.getAt("role").asNullable(RReference.codec()).or(this.role);
        this.channel = source.getAt("channel").asNullable(TCReference.codec()).or(this.channel);
        this.message = source.getAt("message").asLongNullable().or(this.message);
        this.reaction = source.getAt("reaction").asStringNullable().or(this.reaction);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("role").asNullable(RReference.codec()).to(this.role);
        data.setAt("channel").asNullable(TCReference.codec()).to(this.channel);
        data.setAt("message").asLongNullable().to(this.message);
        data.setAt("reaction").asStringNullable().to(this.reaction);

        return data;
    }
}
