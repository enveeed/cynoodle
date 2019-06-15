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

import cynoodle.discord.MEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.MoreCodecs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@EIdentifier(VerificationModule.IDENTIFIER + ":status")
public final class VerificationStatus extends MEntity {
    private VerificationStatus() {}

    static final EntityType<VerificationStatus> TYPE = EntityType.of(VerificationStatus.class);

    // ===

    /**
     * If this member has verified themselves.
     */
    private boolean verified = false;

    /**
     * If {@link #verified} is true, this contains the time they were verified,
     * otherwise null.
     */
    @Nullable
    private Instant verificationTime = null;

    // ===

    public boolean isVerified() {
        return this.verified;
    }

    @Nonnull
    public Optional<Instant> getVerificationTime() {
        return Optional.ofNullable(verificationTime);
    }

    // ===

    public void setVerified() {
        this.verified = true;
        this.verificationTime = Instant.now();
    }

    public void unsetVerified() {
        this.verified = false;
        this.verificationTime = null;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.verified = source.getAt("verified").asBoolean().or(this.verified);
        this.verificationTime = source.getAt("verification_time").asNullable(MoreCodecs.forInstant()).or(this.verificationTime);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("verified").asBoolean().to(this.verified);
        data.setAt("verification_time").asNullable(MoreCodecs.forInstant()).to(this.verificationTime);

        return data;
    }
}
