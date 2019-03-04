/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import cynoodle.core.entities.Entity;
import cynoodle.core.mongo.Bsonable;

import javax.annotation.Nonnull;

/**
 * An Embed, a typed persistent data object which can be embedded into an {@link Entity}.
 */
public abstract class Embed implements Bsonable {

    private EmbedType type;

    // ===

    /**
     * Internal initialization method.
     * @param type the type
     */
    void init(@Nonnull EmbedType type) {
        this.type = type;
    }

    // ===

    /**
     * Get the type of this Embed
     * @return the type
     */
    @Nonnull
    public EmbedType getType() {
        return this.type;
    }

    //

    /**
     * Get the type identifier of this Embed.
     * @return the type identifier string
     */
    @Nonnull
    public String getIdentifier() {
        return this.type.getIdentifier();
    }

    // TODO IO

}