/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Parser for Members of a Guild.
 */
public final class MParser implements Parser<DiscordPointer> {

    private final Guild guild;

    // ===

    public MParser(@Nonnull Guild guild) {
        this.guild = guild;
    }

    // ===

    @Override
    public DiscordPointer parse(@Nonnull String input) throws ParserException {
        if(input.length() == 0) throw new ParserException("No input.");

        if(input.charAt(0) == '+') return DiscordPointerParser.get().parse(input.substring(1));

        throw new ParserException("Please only use IDs until this is implemented!");
    }

}
