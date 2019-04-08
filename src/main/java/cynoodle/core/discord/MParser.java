/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParsingException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parser for a {@link Member}.
 */
public final class MParser implements Parser<DiscordPointer> {

    private final DiscordPointer guild;

    private DiscordPointer selfUser = null;

    // ===

    public MParser(@Nonnull DiscordPointer guild) {
        this.guild = guild;
    }

    // ===

    @Nonnull
    public DiscordPointer parse(@Nonnull String input) throws ParsingException {
        if(input.length() == 0) throw new IllegalArgumentException("No input.");

        if(input.charAt(0) == '+') return DiscordPointerParser.get().parse(input.substring(1));

        if(input.equals("~me")) {
            if(selfUser != null) return selfUser;
            else throw new IllegalArgumentException("`~me` cannot be used here.");
        }

        Guild guild = this.guild.asGuild()
                .orElseThrow(IllegalStateException::new);

        for (Member member : guild.getMembers()) {
            if(member.getUser().getId().equalsIgnoreCase(input))
                return DiscordPointer.to(member.getUser());
            if(member.getNickname() != null && input.toLowerCase().contains(member.getNickname().toLowerCase()))
                return DiscordPointer.to(member.getUser());
            if(member.getUser().getName().toLowerCase().contains(input.toLowerCase()))
                return DiscordPointer.to(member.getUser());
            if(member.getUser().getAsMention().equals(input))
                return DiscordPointer.to(member.getUser());
            if(member.getUser().getAsTag().equals(input))
                return DiscordPointer.to(member.getUser());
        }

        throw new ParsingException("No Member found.");
    }

    // ===

    @Nonnull
    public MParser withSelfUser(@Nullable DiscordPointer selfUser) {
        this.selfUser = selfUser;
        return this;
    }
}
