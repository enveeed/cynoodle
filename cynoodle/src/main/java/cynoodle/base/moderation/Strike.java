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

import cynoodle.util.Checks;
import cynoodle.discord.MEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EntityIOException;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static cynoodle.mongodb.fluent.FluentValues.fromInstant;
import static cynoodle.mongodb.fluent.FluentValues.toInstant;

@EIdentifier("base:moderation:strike")
public final class Strike extends MEntity implements Comparable<Strike> {
    private Strike() {}

    /**
     * The reason of the strike
     */
    private String reason;

    /**
     * The time the strike was given or the cause of it happened
     */
    private Instant timestamp;

    /**
     * The decay setting for this strike
     */
    private Decay decay = Decay.never();

    /**
     * Flags the strike as removed
     */
    private boolean removed = false;

    // == METADATA ==

    @Nonnull
    public String getReason() {
        return this.reason;
    }

    public void setReason(@Nonnull String reason) {
        Checks.notBlank(reason, "reason");
        this.reason = reason;
    }

    public void setTimestamp(@Nonnull Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    // == DECAY ==

    @Nonnull
    public Decay getDecay() {
        return this.decay;
    }

    public void setDecay(@Nullable Decay decay) {
        this.decay = decay;
    }

    //

    @Nonnull
    public Optional<Instant> getDecayAt() {
        if(getDecay().isDecayable())
            return getDecay().getDuration().map(duration -> getTimestamp().plus(duration));
        return Optional.empty();
    }

    //

    public boolean isDecayable() {
        return getDecay().isDecayable();
    }

    public boolean isDecayed() {
        Optional<Instant> at = getDecayAt();
        if(at.isEmpty()) return false; // not decayable, thus not decayed
        return Instant.now().isAfter(at.get());
    }

    // == FLAGS ==

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    // ===

    public boolean isEffective() {
        return !isRemoved() && !isDecayed();
    }

    // ===

    @Override
    public void delete() throws NoSuchElementException, EntityIOException {
        super.delete();
    }

    // === DATA ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.reason = data.getAt("reason").asString().or(this.reason);
        this.timestamp = data.getAt("timestamp").as(toInstant()).or(this.timestamp);
        this.decay = data.getAt("decay").as(Decay.fromBson()).or(this.decay);
        this.removed = data.getAt("removed").asBoolean().or(this.removed);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("reason").asString().to(this.reason);
        data.setAt("timestamp").as(fromInstant()).to(this.timestamp);
        data.setAt("decay").as(Decay.toBson()).to(this.decay);
        data.setAt("removed").asBoolean().to(this.removed);

        return data;
    }

    // ===

    @Override
    public int compareTo(@Nonnull Strike o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
