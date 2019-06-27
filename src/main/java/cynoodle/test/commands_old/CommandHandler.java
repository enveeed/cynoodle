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
import cynoodle.discord.DiscordPointer;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Analyzes potential command messages and starts appropriate command threads for commands.
 */
public final class CommandHandler {
    CommandHandler() {}

    // ===

    private static final char CHAR_SEPARATOR = ' ';

    //

    private final CommandsModule module = Module.get(CommandsModule.class);

    // ===

    public void handle(@Nonnull GuildMessageReceivedEvent event) {

        Guild guild         = event.getGuild();
        TextChannel channel = event.getChannel();
        Message message     = event.getMessage();

        String content      = message.getContentRaw();

        // === SETTINGS ===

        // acquire the guild settings
        CommandSettings settings = this.module.getSettings().firstOrCreate(guild);

        String                      prefix  = settings.getPrefix();
        CommandNSCPolicy   nsc     = settings.getNSCPolicy();

        // === PRE-PARSING ===

        if(!content.startsWith(prefix)) return; // not a command, ignore

        // find the command
        StringBuilder collector = new StringBuilder();
        for (int i = prefix.length(); i < content.length(); i++) {
            char c = content.charAt(i);
            if(c == CHAR_SEPARATOR) break;
            else collector.append(c);
        }

        String rawCommand = collector.toString();

        if(rawCommand.isBlank()) return; // not a command, ignore

        // === INPUT ===

        String rawInput = content.substring(rawCommand.length() + prefix.length());

        // === MAPPINGS ===

        DiscordPointer guildPointer = DiscordPointer.to(guild);

        Optional<CommandMappings> mappingsResult = this.module.getMappingsManager().get(guildPointer);

        CommandMappings mappings;

        if(mappingsResult.isEmpty()) {
            channel.sendMessage("**|** Collecting mappings, this may take a while ...").queue();
            mappings = this.module.getMappingsManager().generate(guildPointer);
        }
        else mappings = mappingsResult.orElseThrow();

        // === LOOKUP ===

        Optional<String> result = mappings.find(rawCommand);

        //

        if(result.isPresent()) {

            Optional<Command> commandResult = this.module.getRegistry().get(result.orElseThrow());

            Command command = commandResult.orElseThrow(() ->
                            new IllegalStateException("Mapping contained non-existent command: " + result.orElseThrow()));

            CommandContext context = new CommandContext(event, rawCommand, rawInput);

            // submit into the pool
            this.module.getPool().submit(command, context);

        } else {

            // ignore
            if(nsc == CommandNSCPolicy.IGNORE) return;

            else if(nsc == CommandNSCPolicy.REPORT) {

                // report only

                StringBuilder out = new StringBuilder();

                out.append("**|** No such command: `").append(prefix).append(rawCommand).append("`");

                event.getChannel().sendMessage(out.toString()).queue();

            }

            else if(nsc == CommandNSCPolicy.REPORT_DETAILED) {

                // report with a list of similar commands

                StringBuilder out = new StringBuilder();

                out.append("**|** **No such command** `").append(prefix).append(rawCommand).append("`");

                Set<String> similar = mappings.findSimilar(rawCommand, 5)
                        .stream().map(s -> "`" + prefix + s + "`").collect(Collectors.toSet());

                if(similar.size() > 0) out.append("\n**|** Similar commands: ").append(Joiner.on(" ").join(similar));

                event.getChannel().sendMessage(out.toString()).queue();

            }

        }
    }
}
