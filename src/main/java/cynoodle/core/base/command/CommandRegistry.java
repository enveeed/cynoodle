/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A registry for {@link Command Commands}, used within {@link CommandModule}.
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
