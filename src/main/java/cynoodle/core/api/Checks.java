/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;

/**
 * Validity checks for single values.
 */
public final class Checks {
    private Checks() {}

    // === NULL ===

    @Nonnull
    public static <T> T notNull(T value, @Nonnull String name) throws NullPointerException {
        if(value == null) throw new NullPointerException(name + " cannot be null!");
        return value;
    }

    @Nonnull
    public static <T> T notNull(T value) throws NullPointerException {
        if(value == null) throw new NullPointerException("Value cannot be null!");
        return value;
    }

}
