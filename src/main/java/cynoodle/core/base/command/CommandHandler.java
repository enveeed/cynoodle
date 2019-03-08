/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.google.common.base.Joiner;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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

    private final CommandModule module = Module.get(CommandModule.class);

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
