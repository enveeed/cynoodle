/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import com.mongodb.client.model.Filters;
import cynoodle.core.discord.MEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import cynoodle.core.mongo.fluent.FluentValues;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Tracks mute status for a member.
 */
@EIdentifier("base:moderation:mute_status")
public final class MuteStatus extends MEntity {
    private MuteStatus() {}

    // ===

    /**
     * Begin of the mute, not muted if null.
     */
    private Instant timestamp = null;

    /**
     * Duration of the mute, infinite if null.
     */
    private Duration duration = null;

    // ===

    public boolean isMuted() {
        return this.timestamp != null;
    }

    public boolean isEffectivelyMuted() {
        if(isExpired()) return false;
        else return isMuted();
    }

    //

    public boolean isFinite() {
        return isMuted() && this.duration != null;
    }

    public boolean isInfinite() {
        return isMuted() && this.duration == null;
    }

    // ===

    @Nonnull
    public Optional<Instant> getTimestamp() {
        return Optional.ofNullable(this.timestamp);
    }

    //

    @Nonnull
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(this.duration);
    }

    @Nonnull
    public Optional<Instant> getExpiry() {
        return getTimestamp().flatMap(instant -> getDuration().map(instant::plus));
    }

    //

    public boolean isExpired() {
        Optional<Instant> expiry = getExpiry();
        if(expiry.isEmpty()) return false;
        else return expiry.orElseThrow().isBefore(Instant.now());
    }

    public boolean hasExpired() {
        return isMuted() && isExpired();
    }

    // ===

    public void setFinite(@Nonnull Instant timestamp, @Nonnull Duration duration) {
        this.unset();

        this.timestamp = timestamp;
        this.duration = duration;
    }

    public void setFinite(@Nonnull Duration duration) {
        this.setFinite(Instant.now(), duration);
    }

    public void setInfinite(@Nonnull Instant timestamp) {
        this.unset();

        this.timestamp = timestamp;
        this.duration = null;
    }

    public void setInfinite() {
        this.setInfinite(Instant.now());
    }

    //

    public void unset() {
        this.timestamp = null;
        this.duration = null;
    }

    // ===

    @Nonnull
    public static Bson filterMuted() {
        return Filters.not(Filters.eq("timestamp", null));
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.timestamp = source.getAt("timestamp").asNullable(FluentValues.toInstant()).or(this.timestamp);
        this.duration = source.getAt("duration").asNullable(FluentValues.toDuration()).or(this.duration);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("timestamp").asNullable(FluentValues.fromInstant()).to(this.timestamp);
        data.setAt("duration").asNullable(FluentValues.fromDuration()).to(this.duration);

        return data;
    }
}
