/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.api.Strings;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable mappings of command aliases to command identifiers.
 */
public final class CommandMappings {

    private final Map<String, String> mappings;

    // ===

    private CommandMappings(@Nonnull Map<String, String> mappings) {
        this.mappings = Collections.unmodifiableMap(mappings);
    }

    // ===

    /**
     * Find a command identifier using the input as a command alias,
     * must match exactly.
     * @param input the input / alias
     * @return the command identifier if found, otherwise empty
     */
    @Nonnull
    public Optional<String> find(@Nonnull String input) {
        return Optional.ofNullable(this.mappings.get(input));
    }

    /**
     * Find a set of similar command aliases, using the given input alias.
     * May include an exact match.
     * @param input the input
     * @param limit the limit of the results
     * @return a set containing similar aliases
     */
    @Nonnull
    public Set<String> findSimilar(@Nonnull String input, int limit) {
        return this.mappings.keySet().stream()
                .filter(test -> Strings.similarity(input, test) >= 0.5d)
                .limit(limit)
                .collect(Collectors.toSet());
    }

    // ===

    @Nonnull
    public static CommandMappings collect(@Nonnull DiscordPointer guild) {

        CommandsModule module = Module.get(CommandsModule.class);

        Map<String, String> mappings = new HashMap<>();

        CommandRegistry registry = module.getRegistry();
        CommandSettings.PropertiesStore properties = module.getSettings().firstOrCreate(guild).getProperties();

        for (Command command : registry.all()) {

            CommandDescriptor descriptor = command.getDescriptor();
            String identifier = descriptor.getIdentifier();

            //

            CommandSettings.Properties commandProperties = properties.findOrCreate(identifier);

            //

            Set<String> aliases = commandProperties.getAliases();

            for (String alias : aliases)
                mappings.put(alias, identifier);

        }

        return new CommandMappings(mappings);
    }
}
