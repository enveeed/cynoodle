/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.LongParser;
import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;

/**
 * Parser for {@link DiscordPointer DiscordPointers} (Discord snowflake IDs).
 */
public final class DiscordPointerParser implements Parser<DiscordPointer> {

    private final static DiscordPointerParser instance = new DiscordPointerParser();

    // ===

    @Override
    public DiscordPointer parse(@Nonnull String input) throws ParserException {

        long id;

        try {
            id = LongParser.get().parse(input);
            if(id <= 0) throw new ParserException("Discord snowflake IDs cannot be less or equal than zero!");
        } catch (ParserException e) {
            throw new ParserException("Invalid snowflake ID: " + e.getMessage(), e);
        }

        return DiscordPointer.to(id);

    }

    // ===

    @Nonnull
    public static DiscordPointerParser get() {
        return instance;
    }
}
