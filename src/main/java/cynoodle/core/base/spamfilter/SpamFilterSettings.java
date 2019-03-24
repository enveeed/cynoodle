/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static cynoodle.core.base.spamfilter.SpamFilterModule.SUB_ANALYZER_SETTINGS;

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
