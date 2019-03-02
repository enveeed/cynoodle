/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Parser for {@link Boolean Booleans}.
 */
public final class BooleanParser {

    private static final BooleanParser instance = new BooleanParser();

    // ===

    private static final String[] VALUES_TRUE
            = new String[]{"true","1","yes","y","on","enable","enabled"};
    private static final String[] VALUES_FALSE
            = new String[]{"false","0","no","n","off","disable","disabled"};

    // ===

    public boolean parse(@Nonnull String input) throws ParsingException {
        for (String val : VALUES_TRUE) if(input.equalsIgnoreCase(val)) return true;
        for (String val : VALUES_FALSE) if(input.equalsIgnoreCase(val)) return false;
        throw new ParsingException("Not a valid boolean value: `" + input + "`.");
    }

    // ===

    @Nonnull
    public static BooleanParser get() {
        return instance;
    }
}
