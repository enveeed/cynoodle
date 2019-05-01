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
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Parser for {@link net.dv8tion.jda.api.entities.Role Roles}.
 */
public final class RParser implements Parser<DiscordPointer> {

    private final DiscordPointer guild;

    // ===

    public RParser(@Nonnull DiscordPointer guild) {
        this.guild = guild;
    }

    // ===

    @Nonnull
    @Override
    public DiscordPointer parse(@Nonnull String input) throws ParsingException {
        if(input.length() == 0) throw new IllegalArgumentException("No input.");

        if(input.charAt(0) == '+') return DiscordPointerParser.get().parse(input.substring(1));

        Guild guild = this.guild.asGuild()
                .orElseThrow(IllegalStateException::new);

        for (Role role : guild.getRoles()) {
            if(role.getId().equalsIgnoreCase(input))
                return DiscordPointer.to(role);
            if(role.getName().toLowerCase().contains(input.toLowerCase()))
                return DiscordPointer.to(role);
        }

        throw new ParsingException("No Role found.");
    }
}
