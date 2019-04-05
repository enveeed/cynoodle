/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.api.Checks;
import cynoodle.core.discord.MEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

import static cynoodle.core.mongo.fluent.FluentValues.fromInstant;
import static cynoodle.core.mongo.fluent.FluentValues.toInstant;

@EIdentifier("base:moderation:strike")
public final class Strike extends MEntity implements Comparable<Strike> {
    private Strike() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

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

    // ===

    void create(@Nonnull String reason) {
        setReason(reason);
        setTimestamp(getCreationTime());
    }

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

    // ===

    /**
     * Create a new {@link StrikeFormatter}.
     * @return a new StrikeFormatter
     */
    @Nonnull
    public static StrikeFormatter format() {
        return new StrikeFormatter();
    }
}
