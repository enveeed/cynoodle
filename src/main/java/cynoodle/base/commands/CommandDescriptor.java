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
import cynoodle.api.Checks;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;

/**
 * Descriptor for a {@link Command} class.
 */
public final class CommandDescriptor {

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final Class<? extends Command> commandClass;

    private final String identifier;

    private final String[] aliases;
    private final String permission;

    // ===

    private CommandDescriptor(@Nonnull Builder builder) {
        this.commandClass = builder.commandClass;
        this.identifier = builder.identifier;
        this.aliases = builder.aliases;
        this.permission = builder.permission;
    }

    // ===

    @Nonnull
    public Class<? extends Command> getCommandClass() {
        return this.commandClass;
    }

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    //

    @Nonnull
    public String[] getAliases() {
        return this.aliases;
    }

    @Nonnull
    public String getPermission() {
        return this.permission;
    }

    // ===

    @Nonnull
    Command create() {

        Constructor<? extends Command> constructor;

        try {
            // require the constructor and make sure its accessible
            constructor = this.commandClass.getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            LOG.atSevere().withCause(e).log("Required constructor not found!");
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            LOG.atSevere().withCause(e).log("Access to constructor failed!");
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        Command command;

        try {
            command = constructor.newInstance();
        } catch (Exception e) {
            LOG.atSevere().withCause(e).log("Failed to create new Command instance!");
            throw new IllegalStateException("Failed to create new Command instance!", e);
        }

        // initialize the command
        command.init(this);

        //

        return command;
    }

    // ===

    /**
     * Builder used within parsing command classes.
     */
    private static class Builder {

        private Class<? extends Command> commandClass;
        private String identifier;

        private String[] aliases;
        private String permission;

        // ===

        @Nonnull
        public Builder setCommandClass(@Nonnull Class<? extends Command> commandClass) {
            this.commandClass = commandClass;
            return this;
        }

        @Nonnull
        public Builder setIdentifier(@Nonnull String identifier) {
            this.identifier = identifier;
            return this;
        }

        //

        @Nonnull
        public Builder setAliases(@Nonnull String[] aliases) {
            this.aliases = aliases;
            return this;
        }

        @Nonnull
        public Builder setPermission(@Nonnull String permission) {
            this.permission = permission;
            return this;
        }

        //

        @Nonnull
        public CommandDescriptor build() {

            Checks.notNull(commandClass, "commandClass");
            Checks.notNull(identifier, "identifier");
            Checks.notNull(aliases, "aliases");
            Checks.notNull(permission, "permission");

            return new CommandDescriptor(this);
        }
    }

    // ===

    @Nonnull
    public static CommandDescriptor parse(@Nonnull Class<? extends Command> commandClass) throws CommandClassException {

        Builder builder = new Builder();

        builder.setCommandClass(commandClass);

        // ===

        // ensure class and constructor properties

        if(!Modifier.isFinal(commandClass.getModifiers())) throw new CommandClassException("Command class is not final!");

        try {

            // try to require the constructor, fail otherwise
            Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new CommandClassException("Command class constructor is not private!");

        } catch (NoSuchMethodException e) {
            throw new CommandClassException("Command class does not contain a no-argument constructor!", e);
        }

        // ===

        // find annotations
        CIdentifier annIdentifier = commandClass.getDeclaredAnnotation(CIdentifier.class);
        CAliases annAliases = commandClass.getDeclaredAnnotation(CAliases.class);
        CPermission annPermission = commandClass.getDeclaredAnnotation(CPermission.class);

        // ensure required annotations
        if(annIdentifier == null) throw new CommandClassException("Command class is missing @CIdentifier annotation!");
        if(annAliases == null) throw new CommandClassException("Command class is missing @CAliases annotation!");

        // ===

        String identifier = annIdentifier.value();

        // TODO validate

        builder.setIdentifier(identifier);

        // ===

        String[] aliases = annAliases.value();

        if(aliases.length == 0)
            throw new CommandClassException("Command class needs to define at least one aliases (@CAliases contained zero).");

        builder.setAliases(aliases);

        // ===

        String permission = identifier;

        if(annPermission != null) permission = annPermission.value();

        // TODO validate

        builder.setPermission(permission);

        // ===

        return builder.build();

    }


}
