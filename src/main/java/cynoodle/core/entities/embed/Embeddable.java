/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import cynoodle.core.entities.Entity;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.Bsonable;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * An Embed, a typed persistent data object which can be embedded into an {@link Entity}.
 * New embed instances must be created via {@link EmbeddableType}.
 */
public abstract class Embeddable implements Bsonable {

    static final String KEY_TYPE = "type";

    private EmbeddableType type;

    // ===

    /**
     * Internal initialization method.
     * @param type the type
     */
    final void init(@Nonnull EmbeddableType type) {
        this.type = type;
    }

    // ===

    /**
     * Get the type of this Embeddable
     * @return the type
     */
    @Nonnull
    public EmbeddableType getType() {
        return this.type;
    }

    //

    /**
     * Get the type identifier of this Embeddable.
     * @return the type identifier string
     */
    @Nonnull
    public String getIdentifier() {
        return this.type.getIdentifier();
    }

    // ===

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        // store the type identifier so it is known what type it is on disk
        data.setAt(KEY_TYPE).asString().to(this.type.getIdentifier());

        return data;
    }
}