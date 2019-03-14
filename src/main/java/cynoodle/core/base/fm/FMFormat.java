/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import javax.annotation.Nonnull;

/**
 * Formatting options for {@link FMCommand}.
 */
public enum FMFormat {

    // SIMPLE

    /**
     * Simple track info, without links or additional metadata.
     */
    SIMPLE("simple"),

    /**
     * Simple track info, with huge cover art.
     */
    SIMPLE_COVER("simple-cover"),

    ;

    // ===

    private final String name;

    //

    FMFormat(@Nonnull String name) {
        this.name = name;
    }

    //

    @Nonnull
    public String getName() {
        return this.name;
    }
}
