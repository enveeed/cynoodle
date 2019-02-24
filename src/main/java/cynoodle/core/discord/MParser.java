/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parser for Members of a Guild.
 */
public final class MParser implements Parser<DiscordPointer> {

    private final DiscordModule discord = Module.get(DiscordModule.class);

    private final DiscordPointer guild;

    private DiscordPointer selfUser = null;

    // ===

    public MParser(@Nonnull DiscordPointer guild) {
        this.guild = guild;
    }

    // ===

    @Override
    public DiscordPointer parse(@Nonnull String input) throws ParserException {
        if(input.length() == 0) throw new ParserException("No input.");

        if(input.charAt(0) == '+') return DiscordPointerParser.get().parse(input.substring(1));

        if(input.equals("~me")) {
            if(selfUser != null) return selfUser;
            else throw new ParserException("`~me` cannot be used here.");
        }

        Guild guild = discord.getAPI().getGuildById(this.guild.getID());

        if(guild == null) throw new ParserException("Guild not available!");

        for (Member member : guild.getMembers()) {
            if(member.getUser().getId().equalsIgnoreCase(input))
                return DiscordPointer.to(member.getUser());
            if(member.getNickname() != null && input.contains(member.getNickname()))
                return DiscordPointer.to(member.getUser());
            if(input.contains(member.getUser().getName()))
                return DiscordPointer.to(member.getUser());
        }

        throw new ParserException("No Member found.");
    }

    // ===

    @Nonnull
    public MParser withSelfUser(@Nullable DiscordPointer selfUser) {
        this.selfUser = selfUser;
        return this;
    }
}
