/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.time.Duration;

@EIdentifier("base:strikes:settings")
public final class StrikeSettings extends GEntity {
    private StrikeSettings() {}

    /**
     * The default strike decay setting.
     */
    private Decay default_decay = Decay.of(Duration.ofDays(100));

    // === DEFAULT STRIKE PROPERTIES ===

    @Nonnull
    public Decay getDefaultDecay() {
        return this.default_decay;
    }

    public void setDefaultDecay(@Nonnull Decay default_decay) {
        this.default_decay = default_decay;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.default_decay = source.getAt("default_decay").as(Decay.fromBson()).or(this.default_decay);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("default_decay").as(Decay.toBson()).to(this.default_decay);

        return data;
    }
}
