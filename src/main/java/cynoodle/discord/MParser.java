/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.discord;

import cynoodle.api.parser.Parser;
import cynoodle.api.parser.ParsingException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

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
