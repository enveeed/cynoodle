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

import com.google.common.flogger.FluentLogger;
import cynoodle.discord.DiscordModule;
import cynoodle.module.Module;
import cynoodle.util.options.Options;
import cynoodle.util.options.OptionsException;
import cynoodle.util.options.OptionsResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.concurrent.Callable;

/**
 * A task to execute a {@link Command} in the system thread pool.
 * The task will parse the {@link Input}, create the {@link Context},
 * check permissions etc. and then execute the command.
 */
final class CommandTask implements Callable<Void> {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final CommandType command;

    private final GuildMessageReceivedEvent event;
    private final String rawCommand;
    private final String rawInput;

    private final CommandsSettings settings;
    private final CommandSettings commandSettings;

    // ===

    CommandTask(@Nonnull CommandType command,
                @Nonnull GuildMessageReceivedEvent event,
                @Nonnull String rawCommand,
                @Nonnull String rawInput,
                @Nonnull CommandsSettings settings,
                @Nonnull CommandSettings commandSettings) {
        this.event = event;
        this.command = command;
        this.rawCommand = rawCommand;
        this.rawInput = rawInput;
        this.settings = settings;
        this.commandSettings = commandSettings;
    }

    // ===

    @Override
    public Void call() {

        // parse input

        Options options = command.getOptions();

        OptionsResult result;

        try {
            result = options.parse(rawInput);
        } catch (OptionsException oex) {
            CommandError error = CommandError.newError(CommandError.DEFAULT,
                    oex.getMessage(),
                    "Input Parsing Failed");
            displayError(error);
            return null;
        }

        Input input = new Input(result);

        // test account filtering

        // check if this is the test account
        boolean isTestAccount = Module.get(DiscordModule.class).isTestAccount();
        boolean ignoreTestAccount = input.hasOption(Command.OPT_IGNORE_TEST);

        if(isTestAccount) {
            // do not execute the command because the normal instance will do it instead
            if(ignoreTestAccount) return null;
        }

        // localize (TODO localize)

        // create context

        Context context = new Context(input, event);

        // execution

        long tStart = Clock.systemUTC().millis();

        try {

            // run the command
            Command instance = this.command.getCommand();

            instance.execute(context);

        } catch (Exception e) {
            if(e instanceof CommandException) {
                displayError(((CommandException) e).getError());
            }
            else {
                LOG.atWarning().log("Unexpected exception in command %s with input %s!", command.getKey(), rawInput);
                displayInternalError();
            }
        }

        long tEnd = Clock.systemUTC().millis();

        if(input.hasOption(Command.OPT_DEBUG)) context.getChannel().sendMessage("t: `" + (tEnd - tStart) + " ms`").queue();

        // ===

        return null;
    }

    // ===

    private void displayError(@Nonnull CommandError error) {
        event.getChannel().sendMessage(error.asEmbed()).queue();
    }

    private void displayInternalError() {

        CommandError error = CommandError.newError(
                CommandError.FATAL,
                "Internal Error - This is probably not your fault.",
                "Unexpected Internal Error");

        MessageEmbed embed = error.asEmbed();

        EmbedBuilder out = new EmbedBuilder(embed);
        out.setImage("https://i.imgur.com/SGIpz4r.png");

        embed = out.build();

        //

        event.getChannel().sendMessage(embed).queue();
    }
}
