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

import com.google.common.base.Joiner;
import cynoodle.CyNoodle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Handles pre-parsing of a command, that is the process
 * of deciding if a message is a command or not.
 */
public final class CommandHandler {
    CommandHandler() {}

    /**
     * The character which separates the command from its input.
     * This is the space character.
     */
    private static final char CHAR_SEPARATOR = ' ';

    // ===

    /**
     * Handle the given message event and submit a command task for it
     * if a command could be pre parsed out of it.
     * @param event the message event
     */
    void handle(@Nonnull GuildMessageReceivedEvent event) {

        Commands commands = Commands.get();

        // ===

        Guild guild = event.getGuild();
        Message message = event.getMessage();

        String content = message.getContentRaw();

        // ===

        CommandsSettings settings = commands.getSettings(guild);

        String prefix = settings.getPrefix();

        //

        if(!content.startsWith(prefix)) return; // not a command, ignore

        StringBuilder collector = new StringBuilder();
        for (int i = prefix.length(); i < content.length(); i++) {
            char c = content.charAt(i);
            if(c == CHAR_SEPARATOR) break;
            else collector.append(c);
        }

        String rawCommand = collector.toString();

        if(rawCommand.isBlank()) return; // not a command, ignore

        //

        String rawInput = content.substring(rawCommand.length() + prefix.length());

        // ===

        CommandRegistry registry = commands.getRegistry();

        // NOTE: As a fallback we allow addressing commands directly by their key,
        // without any aliases (useful e.g. for testing)
        Optional<CommandType> result = registry
                .findByAlias(rawCommand)
                .or(() -> registry.find(rawCommand));

        //

        if(result.isPresent()) {

            CommandType command = result.orElseThrow();

            CommandSettings commandSettings = settings.getCommandSettings(command.getKey());

            if(!commandSettings.isEnabled()) {
                // handle disabled command as if it would not exist
                handleUnknown(event, rawCommand, rawInput, settings);
                return;
            }

            handleKnown(event, command, rawCommand, rawInput, settings, commandSettings);

        } else {
            handleUnknown(event, rawCommand, rawInput, settings);
        }

    }

    //

    /**
     * Called by {@link #handle(GuildMessageReceivedEvent)} if a found command is known,
     * creates the context of it and submits a {@link CommandTask} for it into the system pool.
     * @param event the message event
     * @param command the found command type
     * @param rawCommand the raw command string
     * @param rawInput the raw command input string
     * @param settings the guilds command settings
     */
    private void handleKnown(@Nonnull GuildMessageReceivedEvent event,
                             @Nonnull CommandType command,
                             @Nonnull String rawCommand,
                             @Nonnull String rawInput,
                             @Nonnull CommandsSettings settings,
                             @Nonnull CommandSettings commandSettings) {

        // create the task for execution and parsing
        CommandTask task = new CommandTask(command, event, rawCommand, rawInput, settings, commandSettings);

        // submit into system thread pool
        ScheduledThreadPoolExecutor executor = CyNoodle.get().pool();
        executor.submit(task);
    }

    /**
     * Called by {@link #handle(GuildMessageReceivedEvent)} if a found command is unknown,
     * will output this failure according to guild settings.
     * @param event the message event
     * @param rawCommand the raw command string
     * @param rawInput the raw command input string
     * @param settings the guilds command settings
     */
    private void handleUnknown(@Nonnull GuildMessageReceivedEvent event,
                               @Nonnull String rawCommand,
                               @Nonnull String rawInput,
                               @Nonnull CommandsSettings settings) {

        Commands commands = Commands.get();

        NSCPolicy nscPolicy = settings.getNSCPolicy();
        String prefix = settings.getPrefix();

        //

        switch (nscPolicy) {
            case IGNORE:
                break;
            case REPORT: {

                // report only
                StringBuilder out = new StringBuilder();

                out.append("❌ **|** No such command ")
                        .append("`")
                        .append(prefix)
                        .append(rawCommand)
                        .append("`");

                event.getChannel().sendMessage(out.toString()).queue();

                break;
            }
            case REPORT_DETAILED: {

                // report with a list of similar commands
                StringBuilder out = new StringBuilder();

                out.append("❌ **|** No such command ")
                        .append("`")
                        .append(prefix)
                        .append(rawCommand)
                        .append("`");

                Set<String> similar = commands.getRegistry().getSimilarAliases(rawCommand, 5)
                        .stream().map(s -> "`" + prefix + s + "`").collect(Collectors.toSet());

                if (similar.size() > 0) out.append("\n\nSimilar commands are ").append(Joiner.on(" ").join(similar));

                event.getChannel().sendMessage(out.toString()).queue();

                break;
            }
        }

    }
}
