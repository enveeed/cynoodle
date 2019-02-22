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
 * Formatter for {@link Member} names.
 */
public final class MFormatter implements Formatter<DiscordPointer> {

    private final DiscordModule module = Module.get(DiscordModule.class);

    private final DiscordPointer guild;

    private Mode mode;

    // ===

    private MFormatter(@Nonnull DiscordPointer guild) {
        this.guild = guild;
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
        Guild guild = module.getAPI().getGuildById(this.guild.getID());
        if(guild == null) return formatUser(input);
        Member member = guild.getMemberById(input.getID());
        if(member == null) return formatID(input);
        else return member.getEffectiveName();
    }

    // ===

    @Nonnull
    public MFormatter setMode(@Nonnull Mode mode) {
        this.mode = mode;
        return this;
    }

    // ===

    @Nonnull
    public static MFormatter of(@Nonnull DiscordPointer guild) {
        return new MFormatter(guild);
    }

    @Nonnull
    public static MFormatter of(@Nonnull Guild guild) {
        return of(DiscordPointer.to(guild));
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
