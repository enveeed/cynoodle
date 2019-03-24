/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import cynoodle.core.entities.SubEntity;

import javax.annotation.Nonnull;

/**
 * Settings for {@link SpamAnalyzer}, within {@link SpamFilterSettings}.
 */
public final class SpamAnalyzerSettings extends SubEntity {
    private SpamAnalyzerSettings() {}

    // ===

    /**
     * The identifier of the analyzer these settings are for.
     */
    private String identifier;

    /**
     * The intensity of this analyzer, any value between 0 and 1
     */
    private double intensity = 0.5d;

    // ===

    void create(@Nonnull String identifier) {
        this.identifier = identifier;
    }

    // ===

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    //

    public double getIntensity() {
        return this.intensity;
    }

    public void setIntensity(double intensity) {
        // TODO validate
        this.intensity = intensity;
    }
}
