/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for integers.
 */
public final class IntegerParser implements Parser<Integer> {

    @Override
    public Integer parse(@Nonnull String input) throws ParserException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParserException("Illegal integer number (32-bit number): `" + input + "`", e);
        }
    }

}
