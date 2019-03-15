/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.discord.DiscordPointer;
import net.dv8tion.jda.core.entities.*;
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

    // === DATA ===

    /**
     * Get the message which caused this command.
     * @return the message
     */
    @Nonnull
    public Message getMessage() {
        return this.event.getMessage();
    }

    /**
     * Get the user which causes this command.
     * @return the user
     */
    @Nonnull
    public User getUser() {
        return this.event.getAuthor();
    }

    /**
     * Get the channel this command happened in.
     * @return the channel
     */
    @Nonnull
    public TextChannel getChannel() {
        return this.event.getChannel();
    }

    /**
     * Get the guild this command happened in.
     * @return the guild
     */
    @Nonnull
    public Guild getGuild() {
        return this.event.getGuild();
    }

    // === POINTERS ==

    /**
     * Get the message which causes this command, as a pointer.
     * @return a pointer to the message
     */
    @Nonnull
    public DiscordPointer getMessagePointer() {
        return DiscordPointer.to(getMessage());
    }

    /**
     * Get the user which causes this command, as a pointer.
     * @return a pointer to the user
     */
    @Nonnull
    public DiscordPointer getUserPointer() {
        return DiscordPointer.to(getUser());
    }

    /**
     * Get the channel in which the command happened, as a pointer.
     * @return a pointer to the channel
     */
    @Nonnull
    public DiscordPointer getChannelPointer() {
        return DiscordPointer.to(getChannel());
    }

    /**
     * Get the guild in which the command happened, as a pointer.
     * @return a pointer to the guild
     */
    @Nonnull
    public DiscordPointer getGuildPointer() {
        return DiscordPointer.to(getGuild());
    }

    // === EVENT ===

    /**
     * Get the event which caused the command.
     * @return the event
     */
    @Nonnull
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    // === INTERNALS ===

    /**
     * Get the raw command string from pre-parsing, that is
     * the command alias without the prefix or further input.
     * @return the raw command
     */
    @Nonnull
    String getRawCommand() {
        return this.rawCommand;
    }

    /**
     * Get the raw command input string from pre-parsing, that is
     * the content of the command without the prefix or the alias,
     * may be empty.
     * @return the raw input
     */
    @Nonnull
    String getRawInput() {
        return this.rawInput;
    }

    // === UTILITY ===

    /**
     * Queue a message for reply in the channel of this context.
     * @param message the message
     */
    public void queueReply(@Nonnull Message message) {
        this.getChannel().sendMessage(message).queue();
    }

    /**
     * Queue a message for reply in the channel of this context.
     * @param message the message
     */
    public void queueReply(@Nonnull String message) {
        this.getChannel().sendMessage(message).queue();
    }

    /**
     * Queue an embed for reply in the channel of this context.
     * @param embed the embed
     */
    public void queueReply(@Nonnull MessageEmbed embed) {
        this.getChannel().sendMessage(embed).queue();
    }

    //

    /**
     * Queue an error embed for reply in the channel of this context.
     * @param error the error
     */
    public void queueError(@Nonnull CommandError error) {
        queueReply(error.asEmbed());
    }
}
