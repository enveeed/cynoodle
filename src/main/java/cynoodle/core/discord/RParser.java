/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.parser.Parser;
import cynoodle.core.api.parser.ParsingException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nonnull;

/**
 * Parser for {@link net.dv8tion.jda.core.entities.Role Roles}.
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
