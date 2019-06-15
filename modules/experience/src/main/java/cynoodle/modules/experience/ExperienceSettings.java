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

import cynoodle.discord.GEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.MoreCodecs;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The experience settings of a Guild.
 */
@EIdentifier(ExperienceModule.IDENTIFIER + ":settings")
public final class ExperienceSettings extends GEntity {
    private ExperienceSettings() {}

    final static EntityType<ExperienceSettings> TYPE = EntityType.of(ExperienceSettings.class);

    // ===

    /**
     * Settings for all gain types.
     */
    private Map<GainType, GainSettings> gainSettings = new HashMap<>();

    //

    private boolean drops_enabled = true;

    // === GAIN ===

    @Nonnull
    public GainSettings getGainSettings(@Nonnull GainType type) {
        GainSettings settings = gainSettings.get(type);
        if (settings == null) {
            settings = new GainSettings(type);
            this.gainSettings.put(type, settings);
            this.persist();
        }
        return settings;
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

        this.gainSettings = source.getAt("gain")
                .as(MoreCodecs.forValueMap(GainSettings.codec(), GainSettings::getType))
                .or(this.gainSettings);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("gain")
                .as(MoreCodecs.forValueMap(GainSettings.codec(), GainSettings::getType))
                .to(this.gainSettings);

        return data;
    }
}
