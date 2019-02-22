/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Analyzes potential command messages and starts appropriate command threads for commands.
 */
public final class CommandHandler {
    CommandHandler() {}

    // ===

    private static final char CHAR_SEPARATOR = ' ';

    //

    private final CommandModule module = Module.get(CommandModule.class);

    private final CommandRegistry registry = module.getRegistry();

    // ===

    public void handle(@Nonnull GuildMessageReceivedEvent event) {

        Guild   guild   = event.getGuild();
        Message message = event.getMessage();

        String  content = message.getContentRaw();

        // ===

        // acquire the guild settings
        CommandSettings settings = this.module.getSettings().firstOrCreate(guild);

        String                      prefix  = settings.getPrefix();
        CommandSettings.NSCPolicy   nsc     = settings.getNSCPolicy();

        // ===

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

        // ===

        String rawInput = content.substring(rawCommand.length() + prefix.length());

        // ===

        DiscordPointer guildPointer = DiscordPointer.to(guild);

        // TODO improve, this is horribly unsafe
        if(!module.mappers.containsKey(guildPointer)) {
            event.getChannel()
                    .sendMessage("**|** Collecting mappings, this may take a second or two ...").queue();
            module.mappers.put(guildPointer, CommandMapper.collect(guildPointer));
        }

        CommandMapper mapper = module.mappers.get(guildPointer);

        Optional<Command> result = mapper.find(rawCommand)
                .flatMap(identifier -> module.getRegistry().get(identifier));

        if(result.isPresent()) {

            // there was a command for the input
            System.out.printf("command: %s\n", rawCommand + " with " + rawInput);

            Command command = result.orElseThrow();

            // construct the context for this execution
            CommandContext context = new CommandContext(event, rawCommand, rawInput);

            // submit into the pool
            this.module.getPool().submit(command, context);

        } else {

            // there is no command for the input
            System.out.printf("no such command: %s\n", rawCommand);

            if(nsc == CommandSettings.NSCPolicy.IGNORE) return;

            else if(nsc == CommandSettings.NSCPolicy.REPORT) {

                // TODO send report message

            }

            else if(nsc == CommandSettings.NSCPolicy.REPORT_DETAILED) {

                // TODO send report detailed message

            }
        }
    }
}
