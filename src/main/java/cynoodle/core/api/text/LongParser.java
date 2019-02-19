/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for longs.
 */
public final class LongParser implements Parser<Long> {

    @Override
    public Long parse(@Nonnull String input) throws ParserException {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new ParserException("Illegal integer number (64-bit number): `" + input + "`", e);
        }
    }

}
