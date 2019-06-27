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

import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Formatter for {@link Member} names.
 */
public final class MFormatter {

    private final DiscordModule module = Module.get(DiscordModule.class);

    private final DiscordPointer guild;

    private Mode mode = Mode.MEMBER;

    // ===

    public MFormatter(@Nonnull GReference guild) {
        this.guild = DiscordPointer.to(guild.getID());
    }

    // ===

    @Nonnull
    public String format(@Nonnull DiscordPointer input) {
        if(mode == Mode.ID) return formatID(input);
        else if(mode == Mode.USER_FULL) return formatUserFull(input);
        else if(mode == Mode.USER) return formatUser(input);
        else if(mode == Mode.MEMBER) return formatMember(input);
        else throw new IllegalStateException();
    }

    //

    @Nonnull
    private String formatID(@Nonnull DiscordPointer input) {
        return "`Member-" + input.getID() + "`";
    }

    @Nonnull
    private String formatUserFull(@Nonnull DiscordPointer input) {
        User user = module.getAPI().retrieveUserById(input.getID()).complete(); // TODO this throws when user is not found, fix this
        if(user == null) return formatID(input);
        else return user.getName() + "#" + user.getDiscriminator();
    }

    @Nonnull
    private String formatUser(@Nonnull DiscordPointer input) {
        User user = module.getAPI().retrieveUserById(input.getID()).complete(); // TODO this throws when user is not found, fix this
        if(user == null) return formatID(input);
        else return user.getName();
    }

    @Nonnull
    private String formatMember(@Nonnull DiscordPointer input) {
        Guild guild = module.getAPI().getGuildById(this.guild.getID());
        if(guild == null) return formatUser(input);
        Member member = guild.getMemberById(input.getID());
        if(member == null) return formatUser(input);
        else return member.getEffectiveName();
    }

    // ===

    @Nonnull
    public MFormatter withMode(@Nonnull Mode mode) {
        this.mode = mode;
        return this;
    }

    // ===

    public enum Mode {

        /**
         * Display as ID.
         */
        ID,

        /**
         * Display as username with discriminator,
         * falls back to ID if unavailable.
         */
        USER_FULL,

        /**
         * Display as username without discriminator,
         * falls back to ID if unavailable.
         */
        USER,

        /**
         * Display as member nickname or username if no nickname is set,
         * falls back to ID if unavailable.
         */
        MEMBER,

        ;

    }

}
