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

package cynoodle.base.commands;

import cynoodle.discord.GReference;
import cynoodle.discord.UReference;
import cynoodle.util.Checks;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

/**
 * Context for a {@link CommandType} execution.
 */
public final class Context {

    private final Input input;

    private final GuildMessageReceivedEvent event;

    // ===

    Context(@Nonnull Input input,
            @Nonnull GuildMessageReceivedEvent event) {
        this.input = input;
        this.event = event;
    }

    // ===

    /**
     * Get the {@link Input} of this command execution.
     * @return the input
     */
    @Nonnull
    public Input getInput() {
        return this.input;
    }

    /**
     * Get the {@link GuildMessageReceivedEvent} that triggered this command execution.
     * @return the causing message event
     */
    @Nonnull
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
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
    public Member getMember() {
        return Checks.notNull(this.event.getMember(), "event.GetMember()");
    }

    // ===

    @Nonnull
    public UReference getUserReference() {
        return UReference.to(getUser());
    }

    @Nonnull
    public GReference getGuildReference() {
        return GReference.to(getGuild());
    }

    // ===

    public void throwError(@Nonnull CommandError error)
            throws CommandException {
        throw new CommandException(error);
    }

    public void throwError(@Nonnull CommandError.Type type, @Nonnull String message)
            throws CommandException {
        throwError(CommandError.newError(type, message));
    }

    public void throwError(@Nonnull CommandError.Type type, @Nonnull String message,
                           @Nonnull String title)
            throws CommandException {
        throwError(CommandError.newError(type, message, title));
    }

    public void throwError(@Nonnull String message)
            throws CommandException {
        throwError(CommandError.newError(CommandError.DEFAULT, message));
    }

    public void throwError(@Nonnull String message,
                           @Nonnull String title)
            throws CommandException {
        throwError(CommandError.newError(CommandError.DEFAULT, message, title));
    }

    // ===

    public void queueReply(@Nonnull String message) {
        this.getChannel().sendMessage(message).queue();
    }

    public void queueReply(@Nonnull Message message) {
        this.getChannel().sendMessage(message).queue();
    }

    public void queueReply(@Nonnull MessageEmbed embed) {
        this.getChannel().sendMessage(embed).queue();
    }
}
