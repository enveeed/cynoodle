/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;
import cynoodle.core.base.command.CommandContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parser for Members of a Guild.
 */
public final class MParser implements Parser<DiscordPointer> {

    private DiscordPointer guild = null;

    private DiscordPointer selfUser = null;

    // ===

    private MParser() {}

    // ===

    @Override
    public DiscordPointer parse(@Nonnull String input) throws ParserException {
        if(input.length() == 0) throw new ParserException("No input.");

        if(input.charAt(0) == '+') return DiscordPointerParser.get().parse(input.substring(1));

        if(input.equalsIgnoreCase("~me")) {
            if(selfUser != null) return selfUser;
            else throw new ParserException("`~me` cannot be used here.");
        }

        throw new ParserException("Please only use IDs until this is implemented!");
    }

    // ===

    @Nonnull
    public MParser setGuild(@Nullable DiscordPointer guild) {
        this.guild = guild;
        return this;
    }

    @Nonnull
    public MParser setSelfUser(@Nullable DiscordPointer selfUser) {
        this.selfUser = selfUser;
        return this;
    }

    // ===

    @Nonnull
    public static MParser create() {
        return new MParser();
    }

    @Nonnull
    public static MParser create(@Nonnull CommandContext context) {
        return new MParser()
                .setGuild(DiscordPointer.to(context.getGuild()))
                .setSelfUser(DiscordPointer.to(context.getUser()));
    }
}
