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

package cynoodle.test.commands;

import cynoodle.test.permissions.Permission;
import cynoodle.util.options.Option;
import cynoodle.util.options.Options;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * A type of command.
 */
public final class CommandType {

    private final Command     command;

    private final String      key;
    private final String[]    aliases;
    private final Permission  permission;
    private final Options     options;

    // ===

    public CommandType(Command command,
                       String key,
                       String[] aliases,
                       Permission permission,
                       Options options) {
        this.command = command;
        this.key = key;
        this.aliases = aliases;
        this.permission = permission;
        this.options = options;
    }

    // ===

    @Nonnull
    public Command getCommand() {
        return this.command;
    }

    // ===

    /**
     * Get the command key of this command.
     * @return the command key.
     */
    @Nonnull
    public String getKey() {
        return this.key;
    }

    /**
     * Get the aliases of this command.
     * Always at least 1.
     * @return the command alias array
     */
    @Nonnull
    public String[] getAliases() {
        return this.aliases.clone();
    }

    /**
     * Get the permission which is required to execute this command.
     * @return the command permission.
     */
    @Nonnull
    public Permission getPermission() {
        return this.permission;
    }

    /**
     * Get the {@link Options} instance which is used to parse the command input.
     * @return the options instance
     */
    @Nonnull
    public Options getOptions() {
        return this.options;
    }

    // ===

    /**
     * Create a {@link CommandType} instance based on annotations of a given {@link Command} implementation class.
     * @param commandClass the command class to create a type of
     * @return the new command type
     * @throws IllegalArgumentException if the command type could not be generated from the class
     */
    @Nonnull
    public static CommandType ofAnnotated(@Nonnull Class<? extends Command> commandClass) throws IllegalArgumentException {

        // TODO ENSURE FINAL AND CONSTRUCTORS ETC THIS IS IMPORTANT

        Command command;

        try {
            Constructor<? extends Command> c = commandClass.getConstructor();
            c.setAccessible(true);
            command = c.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("FAILED TO CREATE NEW INSTANCE"); // TODO
        }

        // key
        CommandKey keyAnn = commandClass.getDeclaredAnnotation(CommandKey.class);
        if(keyAnn == null)
            throw new IllegalArgumentException("Command class is missing @CommandKey annotation: " + commandClass);

        String key = keyAnn.value();

        // aliases
        CommandAliases aliasesAnn = commandClass.getDeclaredAnnotation(CommandAliases.class);
        if(aliasesAnn == null)
            throw new IllegalArgumentException("Command class is missing @CommandAliases annotation: " + commandClass);

        String[] aliases = aliasesAnn.value();
        if(aliases.length == 0)
            throw new IllegalArgumentException("@CommandAliases annotation does not have at least one entry: " + commandClass);

        // permission
        CommandPermission permissionAnn = commandClass.getDeclaredAnnotation(CommandPermission.class);
        if(permissionAnn == null)
            throw new IllegalArgumentException("Command class is missing @CommandPermission annotation: " + commandClass);

        Permission permission;
        try {
            permission = Permission.of(permissionAnn.value());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("@CommandPermission annotation contained illegal permission string: " + commandClass);
        }

        // options

        Set<Option> collectedOptions = new HashSet<>();

        for (Field field : commandClass.getDeclaredFields()) {
            // ensure annotated
            if(!field.isAnnotationPresent(CommandOption.class)) continue;
            // ensure modifiers
            if(!Modifier.isStatic(field.getModifiers())) continue;
            if(!Modifier.isFinal(field.getModifiers())) continue;
            // ensure it is option
            if(field.getType() != Option.class) continue;

            Option option;

            try {
                option = (Option) field.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("@CommandOption annotated field " + field + " could not be accessed: " + commandClass);
            }

            collectedOptions.add(option);
        }

        // add default options

        collectedOptions.add(Command.OPT_DEBUG);
        collectedOptions.add(Command.OPT_LOCALIZE);
        collectedOptions.add(Command.OPT_IGNORE_TEST);

        // create options

        Options options = Options.newBuilder()
                .add(collectedOptions)
                .build();

        // finalize

        return new CommandType(
                command,
                key,
                aliases,
                permission,
                options);
    }
}
