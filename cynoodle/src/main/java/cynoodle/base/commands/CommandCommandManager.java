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
import cynoodle.util.Strings;
import cynoodle.util.parsing.PrimitiveParsers;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command to edit a guilds {@link CommandsSettings}.
 */
@CommandKey("base:commands:command_manager")
@CommandAliases({"cmanager", "cman","cm"})
@CommandPermission("commands.command.command_manager")
public final class CommandCommandManager implements Command {
    private CommandCommandManager() {}

    public static final CommandType TYPE = CommandType.ofAnnotated(CommandCommandManager.class);

    // ===

    @Override
    public void execute(@Nonnull Context context) throws CommandException {

        Input input = context.getInput();

        Commands commands = Commands.get();

        CommandsSettings settings = commands.getSettings(context.getGuild());

        if(!input.hasParameter(0)) {

            StringBuilder out = new StringBuilder();

            out.append("`").append(settings.getPrefix()).append("cmanager [...]`").append("\n\n");

            out.append(formatRow("list",
                    "List all commands")).append("\n");
            out.append(formatRow("settings",
                    "View or modify general command settings")).append("\n");
            out.append(formatRow("command [command]",
                    "View or modify a specific command")).append("\n");

            context.queueReply(out.toString());
            return;
        }

        //

        String option0 = input.requireParameter(0, "option");

        if(option0.equals("list")) {

            List<CommandType> list = commands.getRegistry().getCommands()
                    .stream()
                    .sorted(Comparator.comparing(CommandType::getKey))
                    .collect(Collectors.toList());

            StringBuilder out = new StringBuilder();

            for (CommandType commandType : list) {
                out.append("`").append(Strings.box(commandType.getKey(), 30)).append("`")
                        .append(" **|** ")
                        .append("`").append(Joiner.on(' ').join(Arrays.stream(commandType.getAliases())
                        .map(s -> "`" + settings.getPrefix() + s + "`").collect(Collectors.toSet())))
                        .append("\n");
            }

            context.queueReply(out.toString());
            return;
        }
        else if(option0.equals("settings")) {

            if(!input.hasParameter(1)) {

                StringBuilder out = new StringBuilder();

                out.append("`").append(settings.getPrefix()).append("cmanager settings [...]").append("`\n\n");

                out.append(formatRow("prefix (prefix)",
                        "View or set the prefix")).append("\n");
                out.append(formatRow("nsc (policy name)",
                        "View or set the no-such-command policy, one of `ignore`, `report` or `report_detailed`")).append("\n");

                context.queueReply(out.toString());
                return;

            }

            String option1 = input.requireParameter(1, "setting");

            if(option1.equals("prefix")) {

                Optional<String> prefixO = input.getParameter(2);

                if(prefixO.isEmpty()) {
                    context.queueReply("**|** The current prefix for all commands is **`" + settings.getPrefix() + "`**.");
                    return;
                }

                String prefix = prefixO.get();

                if(!CommandsSettings.isValidPrefix(prefix)) {
                    context.throwError("Not a valid prefix: `" + prefix + "`!");
                    return;
                }

                settings.setPrefix(prefix);
                settings.persist();

                context.queueReply("**|** The prefix for all commands was set to **`" + settings.getPrefix() + "`**.");
                return;

            }
            else if(option1.equals("nsc")) {

                Optional<String> nscKeyO = input.getParameter(2);

                if(nscKeyO.isEmpty()) {
                    context.queueReply("**|** The current NSC policy is **`" + settings.getNSCPolicy().getKey() + "`**.");
                    return;
                }

                String nscKey = nscKeyO.get();

                Optional<NSCPolicy> nscO = NSCPolicy.get(nscKey);

                if(nscO.isEmpty()) {
                    context.throwError("Unknown NSC policy: `" + nscKey + "`!");
                    return;
                }

                NSCPolicy nsc = nscO.get();

                settings.setNSCPolicy(nsc);
                settings.persist();

                context.queueReply("**|** The NSC policy was set to **`" + settings.getNSCPolicy().getKey() + "`**.");
                return;

            }
            else {
                context.throwError("Not a valid setting: `" + option1 + "`!");
                return;
            }

        }
        else if(option0.equals("command")) {

            String commandKey = input.requireParameter(1, "command");

            Optional<CommandType> commandO = commands.getRegistry().find(commandKey)
                    .or(() -> commands.getRegistry().findByAlias(commandKey));

            if(commandO.isEmpty()) {
                context.throwError("Could not find a command like this: `" + commandKey + "`!");
                return;
            }

            CommandType commandType = commandO.get();

            if(!input.hasParameter(2)) {

                StringBuilder out = new StringBuilder();

                out.append("`").append(settings.getPrefix()).append("cmanager command ")
                        .append(commandType.getKey())
                        .append(" [...]")
                        .append("`\n\n");

                out.append(formatRow("info",
                        "View command info")).append("\n");
                out.append(formatRow("enabled (on / off)",
                        "View or set the enabled status")).append("\n");

                context.queueReply(out.toString());
                return;

            }

            CommandSettings commandSettings = settings.getCommandSettings(commandType.getKey());

            String option2 = input.requireParameter(1, "setting");

            if(option2.equals("info")) {

                StringBuilder out = new StringBuilder();

                out.append("**Command `").append(commandType.getKey()).append("`**")
                        .append("\n\n");

                out.append("**>** Aliases").append("\n")
                        .append(Joiner.on(' ').join(Arrays.stream(commandType.getAliases())
                                .map(s -> "`" + settings.getPrefix() + s + "`").collect(Collectors.toSet())))
                        .append("\n\n");
                out.append("**>** Permission").append("\n")
                        .append("`").append(commandType.getPermission().toString()).append("`")
                        .append("\n\n");

                context.queueReply(out.toString());
                return;

            }
            else if(option2.equals("enabled")) {

                Optional<Boolean> enabledO = input.getParameterAs(3, "enabled", PrimitiveParsers.parseBoolean());

                if(enabledO.isEmpty()) {
                    if(commandSettings.isEnabled())
                        context.queueReply("**|** The command **`" + commandType.getKey() + "`** is currently **enabled**.");
                    else
                        context.queueReply("**|** The command **`" + commandType.getKey() + "`** is currently **disabled**.");
                    return;
                }

                boolean enabled = enabledO.get();

                commandSettings.setEnabled(enabled);
                settings.persist();

                if(enabled)
                    context.queueReply("**|** The command **`" + commandType.getKey() + "`** has been **enabled**.");
                else
                    context.queueReply("**|** The command **`" + commandType.getKey() + "`** has been **disabled**.");

                return;
            }
            else {
                context.throwError("Not a valid setting: `" + option2 + "`!");
                return;
            }

        }
        else {
            context.throwError("Not a valid option: `" + option0 + "`!");
            return;
        }
    }

    // ===

    @Nonnull
    private String formatRow(@Nonnull String command, @Nonnull String description) {
        return "`\u200b" + Strings.box(command, 40) + "\u200b`"
                + " **|** " + description;
    }
}
