/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

/**
 * The context for a single command execution.
 * @see Command
 */
public final class CommandContext {

    private final GuildMessageReceivedEvent event;

    private final String rawCommand;
    private final String rawInput;

    // ===

    CommandContext(@Nonnull GuildMessageReceivedEvent event,
                   @Nonnull String rawCommand, @Nonnull String rawInput) {
        this.event = event;
        this.rawCommand = rawCommand;
        this.rawInput = rawInput;
    }

    // ===

    @Nonnull
    public Message getMessage() {
        return this.event.getMessage();
    }

    @Nonnull
    public User getUser() {
        return this.event.getAuthor();
    }

    @Nonnull
    public TextChannel getChannel() {
        return this.event.getChannel();
    }

    @Nonnull
    public Guild getGuild() {
        return this.event.getGuild();
    }

    //

    @Nonnull
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    //

    @Nonnull
    String getRawCommand() {
        return this.rawCommand;
    }

    @Nonnull
    String getRawInput() {
        return this.rawInput;
    }
}
