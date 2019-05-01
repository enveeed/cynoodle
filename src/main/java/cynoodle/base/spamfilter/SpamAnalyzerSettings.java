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

import cynoodle.entities.NestedEntity;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * Settings for {@link SpamAnalyzer}, within {@link SpamFilterSettings}.
 */
public final class SpamAnalyzerSettings extends NestedEntity {
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

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

        this.identifier = data.getAt("identifier").asString().value();

        this.intensity = data.getAt("intensity").asDouble().or(this.intensity);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("identifier").asString().to(this.identifier);

        data.setAt("intensity").asDouble().to(this.intensity);

        return data;
    }
}
