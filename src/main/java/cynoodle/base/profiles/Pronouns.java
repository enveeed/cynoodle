/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.profiles;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum Pronouns {

    /**
     * Addresses the user grammatically masculine.
     */
    MASCULINE("masculine"),
    /**
     * Addresses the user grammatically feminine.
     */
    FEMININE("feminine"),
    /**
     * Addresses the user grammatically indefinite.
     */
    INDEFINITE("indefinite"),

    ;

    private final String key;

    Pronouns(@Nonnull String key) {
        this.key = key;
    }

    // ===

    @Nonnull
    public String key() {
        return this.key;
    }

    // ===

    @Nonnull
    public static Optional<Pronouns> find(@Nonnull String key) {
        if(key.equals(MASCULINE.key()))
            return Optional.of(MASCULINE);
        if(key.equals(FEMININE.key()))
            return Optional.of(FEMININE);
        if(key.equals(INDEFINITE.key()))
            return Optional.of(INDEFINITE);
        return Optional.empty();
    }

}
