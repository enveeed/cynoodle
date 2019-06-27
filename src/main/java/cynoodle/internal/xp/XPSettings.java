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

package cynoodle.test.xp;

import cynoodle.discord.GEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.FluentValues;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * A Guilds general settings for the XP system
 */
@EIdentifier("base:xp:settings")
public final class XPSettings extends GEntity {
    private XPSettings() {}

    /**
     * The minimum value of random XP gain.
     */
    private long gain_min = 25;

    /**
     * The maximum value of random XP gain.
     */
    private long gain_max = 85;

    /**
     * The timeout duration for random XP gain.
     */
    private Duration gain_timeout = Duration.ofSeconds(60);

    //

    private boolean drops_enabled = true;

    // === GAIN ===

    public long getGainMin() {
        return this.gain_min;
    }

    public void setGainMin(long gain_min) {
        this.gain_min = gain_min;
    }

    public long getGainMax() {
        return this.gain_max;
    }

    public void setGainMax(long gain_max) {
        this.gain_max = gain_max;
    }

    @Nonnull
    public Duration getGainTimeout() {
        return this.gain_timeout;
    }

    public void setGainTimeout(@Nonnull Duration gain_timeout) {
        this.gain_timeout = gain_timeout;
    }

    // === DROPS ===

    public boolean isDropsEnabled() {
        return this.drops_enabled;
    }

    public void setDropsEnabled(boolean drops_enabled) {
        this.drops_enabled = drops_enabled;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.gain_min = source.getAt("gain_min").asLong().or(this.gain_min);
        this.gain_max = source.getAt("gain_max").asLong().or(this.gain_max);
        this.gain_timeout = source.getAt("gain_timeout").as(FluentValues.toDuration()).or(this.gain_timeout);
        this.drops_enabled = source.getAt("drops_enabled").asBoolean().or(this.drops_enabled);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("gain_min").asLong().to(this.gain_min);
        data.setAt("gain_max").asLong().to(this.gain_max);
        data.setAt("gain_timeout").as(FluentValues.fromDuration()).to(this.gain_timeout);
        data.setAt("drops_enabled").asBoolean().to(this.drops_enabled);

        return data;
    }

}