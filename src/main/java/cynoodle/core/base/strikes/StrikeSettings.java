/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EIdentifier("base:strikes:settings")
public final class StrikeSettings extends GEntity {
    private StrikeSettings() {}

    /**
     * The default setting for if a new strike should decay
     */
    private boolean default_decay_enabled = true;

    /**
     * The default setting for the time in which a new strike should decay, if decay is enabled
     */
    private Duration default_decay_duration = Duration.of(100, ChronoUnit.DAYS);

    // === DEFAULT STRIKE PROPERTIES ===

    public boolean isDefaultDecayEnabled() {
        return this.default_decay_enabled;
    }

    public void setDefaultDecayEnabled(boolean default_decay_enabled) {
        this.default_decay_enabled = default_decay_enabled;
    }

    @Nonnull
    public Duration getDefaultDecayDuration() {
        return this.default_decay_duration;
    }

    public void setDefaultDecayDuration(@Nonnull Duration default_decay_duration) {
        this.default_decay_duration = default_decay_duration;
    }

    @Nonnull
    public Decay getEffectiveDefaultDecay() {
        if(isDefaultDecayEnabled()) return Decay.of(getDefaultDecayDuration());
        else return Decay.never();
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.default_decay_enabled = source.getAt("default_decay_enabled").asBoolean().or(this.default_decay_enabled);
        this.default_decay_duration = source.getAt("default_decay_duration").asLong().map(Duration::ofMillis).or(this.default_decay_duration);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("default_decay_enabled").asBoolean().to(this.default_decay_enabled);
        data.setAt("default_decay_duration").asLong().map(Duration::toMillis).to(this.default_decay_duration);

        return data;
    }
}
