/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.output.Formatter;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

/**
 * Formatter for Members.
 */
public final class MNameFormatter implements Formatter<DiscordPointer> {

    private final DiscordModule module = Module.get(DiscordModule.class);
    private final Guild guild;

    private Mode mode;

    // ===

    public MNameFormatter(@Nonnull Guild guild, @Nonnull Mode mode) {
        this.guild = guild;
        this.mode = mode;
    }

    public MNameFormatter(@Nonnull Guild guild) {
        this(guild, Mode.MEMBER);
    }

    // ===

    @Nonnull
    @Override
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
        User user = module.getAPI().getUserById(input.getID());
        if(user == null) return formatID(input);
        else return user.getName() + "#" + user.getDiscriminator();
    }

    @Nonnull
    private String formatUser(@Nonnull DiscordPointer input) {
        User user = module.getAPI().getUserById(input.getID());
        if(user == null) return formatID(input);
        else return user.getName();
    }

    @Nonnull
    private String formatMember(@Nonnull DiscordPointer input) {
        Member member = guild.getMemberById(input.getID());
        if(member == null) return formatID(input);
        else return member.getEffectiveName();
    }

    // ===

    @Nonnull
    public MNameFormatter setMode(@Nonnull Mode mode) {
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
