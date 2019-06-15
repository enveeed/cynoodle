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

package cynoodle.modules.experience;

import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.MoreCodecs;
import cynoodle.util.Checks;
import org.bson.BSONException;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * Settings for the gain values of a specific {@link GainType}.
 */
public final class GainSettings implements IBsonDocument {
    private GainSettings() {}

    // ===

    /**
     * The {@link GainType} these settings are for.
     */
    private GainType type;

    /**
     * If gaining is enabled.
     */
    private boolean enabled = false;

    /**
     * The minimum value for random gain.
     */
    private long min = 35L;
    /**
     * The maximum value for random gain.
     */
    private long max = 85L;

    /**
     * The duration of the timeout after a gain.
     */
    private Duration timeout = Duration.ofSeconds(60L);

    // ===

    GainSettings(@Nonnull GainType type) {
        this.type = type;
    }

    // ===

    @Nonnull
    public GainType getType() {
        return this.type;
    }

    //

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getMinimumValue() {
        return this.min;
    }

    public long getMaximumValue() {
        return this.max;
    }

    public void setValues(long min, long max) {
        Checks.notNegative(min, "min");
        Checks.notNegative(max, "max");
        if(max < min) throw new IllegalArgumentException("Maximum value cannot be less than minimum value!");

        this.min = min;
        this.max = max;
    }

    @Nonnull
    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(@Nonnull Duration timeout) {
        this.timeout = timeout;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {
        this.type = data.getAt("type").as(GainType.codec()).value();
        this.enabled = data.getAt("enabled").asBoolean().or(this.enabled);
        this.min = data.getAt("min").asLong().or(this.min);
        this.max = data.getAt("max").asLong().or(this.max);
        this.timeout = data.getAt("timeout").as(MoreCodecs.forDuration()).or(this.timeout);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();
        data.setAt("type").as(GainType.codec()).to(this.type);
        data.setAt("enabled").asBoolean().to(this.enabled);
        data.setAt("min").asLong().to(this.min);
        data.setAt("max").asLong().to(this.max);
        data.setAt("timeout").as(MoreCodecs.forDuration()).to(this.timeout);
        return data;
    }

    // ===

    @Nonnull
    static Codec<GainSettings> codec() {
        return new IBsonDocumentCodec<>(GainSettings::new);
    }
}
