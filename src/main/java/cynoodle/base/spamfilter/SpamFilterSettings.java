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

package cynoodle.base.spamfilter;

import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentArray;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static cynoodle.base.spamfilter.SpamFilterModule.SUB_ANALYZER_SETTINGS;

@EIdentifier("base:spamfilter:settings")
public final class SpamFilterSettings extends GEntity {
    private SpamFilterSettings() {}

    // ===

    /**
     * If the spam filter should be enabled.
     */
    private boolean enabled = true;

    /**
     * Settings for all analyzers.
     */
    private Map<String, SpamAnalyzerSettings> analyzerSettings = new HashMap<>();

    //

    /**
     * The threshold for the status, at which the member gets muted.
     */
    private double thresholdMute = 3d;

    // ===

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //

    @Nonnull
    public SpamAnalyzerSettings getOrCreateAnalyzerSettings(@Nonnull String identifier) {
        if(this.analyzerSettings.containsKey(identifier)) return this.analyzerSettings.get(identifier);
        else {
            SpamAnalyzerSettings analyzerSettings = SUB_ANALYZER_SETTINGS.create(this);
            analyzerSettings.create(identifier);

            this.analyzerSettings.put(identifier, analyzerSettings);

            this.persist();

            return analyzerSettings;
        }
    }

    //

    public double getMuteThreshold() {
        return this.thresholdMute;
    }

    public void setMuteThreshold(double thresholdMute) {
        this.thresholdMute = thresholdMute;
    }


    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.enabled = source.getAt("enabled").asBoolean().or(this.enabled);

        this.analyzerSettings = source.getAt("analyzers").asArray().or(FluentArray.wrapNew())
                .collect().as(SUB_ANALYZER_SETTINGS.load(this)).toMap(SpamAnalyzerSettings::getIdentifier);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("enabled").asBoolean().to(this.enabled);

        data.setAt("analyzers").asArray().to(FluentArray.wrapNew()
                .insert().as(SUB_ANALYZER_SETTINGS.store()).atEnd(this.analyzerSettings.values()));

        return data;
    }
}
