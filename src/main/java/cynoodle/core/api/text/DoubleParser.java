/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Simple parser for doubles.
 */
public final class DoubleParser implements Parser<Double> {

    @Override
    public Double parse(@Nonnull String input) throws ParserException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ParserException("Illegal decimal number: `" + input + "`", e);
        }
    }

}
