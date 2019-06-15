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

import cynoodle.util.Strings;
import cynoodle.discord.DiscordPointer;
import cynoodle.module.Module;

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
