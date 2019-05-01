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

import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.time.Duration;

@EIdentifier("base:moderation:strike_settings")
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
