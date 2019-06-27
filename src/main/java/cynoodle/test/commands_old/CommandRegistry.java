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

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A registry for {@link Command Commands}, used within {@link CommandsModule}.
 */
public final class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    // ===

    private final Map<Class<? extends Command>, String> classes = new HashMap<>();

    // ===

    @CanIgnoreReturnValue
    @Nonnull
    public final String register(@Nonnull Class<? extends Command> commandClass) throws CommandClassException {

        // parse the class into a descriptor
        CommandDescriptor descriptor = CommandDescriptor.parse(commandClass);

        // create a command instance
        Command command = descriptor.create();

        // register the command
        this.commands.put(descriptor.getIdentifier(), command);

        // map other properties
        this.classes.put(descriptor.getCommandClass(), descriptor.getIdentifier());

        // ===

        return descriptor.getIdentifier();
    }

    // ===

    @Nonnull
    public Set<Command> all() {
        return Set.copyOf(this.commands.values());
    }

    // ===

    @Nonnull
    public Optional<Command> get(@Nonnull String identifier) {
        return Optional.ofNullable(this.commands.get(identifier));
    }

    //

    @Nonnull
    public <C extends Command> Optional<C> get(@Nonnull Class<C> commandClass) {

        String identifier = this.classes.get(commandClass);
        if(identifier == null) return Optional.empty();

        // throw because if the identifier exists, this should exist as well
        Command command = get(identifier).orElseThrow(IllegalStateException::new);

        if(command.getDescriptor().getCommandClass() != commandClass)
            throw new IllegalArgumentException("Command class mismatch!");

        // cast and return as optional
        return Optional.of(commandClass.cast(command));
    }
}
