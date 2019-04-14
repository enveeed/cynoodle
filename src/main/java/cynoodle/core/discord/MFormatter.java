/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.module.Module;
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

    public MFormatter(@Nonnull DiscordPointer guild) {
        this.guild = guild;
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
