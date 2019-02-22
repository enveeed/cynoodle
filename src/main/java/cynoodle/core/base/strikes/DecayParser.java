/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.DurationParser;
import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;

public final class DecayParser implements Parser<Decay> {

    private final static DecayParser instance = new DecayParser();

    // ===

    @Override
    public Decay parse(@Nonnull String input) throws ParserException {
        if(input.equalsIgnoreCase("never")) return Decay.never();
        else return Decay.of(DurationParser.get().parse(input));
    }

    // ===

    @Nonnull
    public static DecayParser get() {
        return instance;
    }
}
