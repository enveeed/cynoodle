/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.discord;

import cynoodle.api.parser.ParsingException;
import cynoodle.api.parser.PrimitiveParsers;

import javax.annotation.Nonnull;

/**
 * Parser for {@link DiscordPointer DiscordPointers} (Discord snowflake IDs).
 */
public final class DiscordPointerParser {

    private final static DiscordPointerParser instance = new DiscordPointerParser();

    // ===

    @Nonnull
    public DiscordPointer parse(@Nonnull String input) throws ParsingException {

        long id;

        try {
            id = PrimitiveParsers.parseLong().parse(input);
            if(id <= 0) throw new ParsingException("Discord snowflake IDs cannot be less or equal than zero!");
        } catch (ParsingException e) {
            throw new ParsingException("Invalid snowflake ID: " + e.getMessage(), e);
        }

        return DiscordPointer.to(id);

    }

    // ===

    @Nonnull
    public static DiscordPointerParser get() {
        return instance;
    }
}
