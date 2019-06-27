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

import cynoodle.util.Strings;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry for {@link CommandType CommandTypes}.
 * <p>
 *     Registers types to be searchable by key and by all their aliases.
 * </p>
 */
public final class CommandRegistry {
    CommandRegistry() {}

    // TODO maybe split this class up or something idk

    /**
     * All registered commands, mapped key -> command type.
     */
    private final Map<String, CommandType> commands = new HashMap<>();

    /**
     * All registered command keys and aliases, mapped alias -> key
     */
    private final Map<String, String> aliases = new HashMap<>();

    // === REGISTRATION ===

    /**
     * Register the given command type.
     * @param type the type
     */
    public void register(@Nonnull CommandType type) {
        String key = type.getKey();

        if(!this.commands.containsKey(key))
            throw new IllegalArgumentException("There is already a CommandType registered for key: " + key);

        // register command type
        this.commands.put(key, type);
        // register all aliases
        // TODO possibly check if they override each other
        for (String alias : type.getAliases())
            this.aliases.put(alias, key);
    }

    // ===

    @Nonnull
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(this.aliases.keySet());
    }

    /**
     * Find a set of similar command aliases, using the given input alias.
     * May include an exact match.
     * @param input the input
     * @param limit the limit of the results
     * @return a set containing similar aliases
     */
    @Nonnull
    public Set<String> getSimilarAliases(@Nonnull String input, int limit) {
        return this.aliases.keySet().stream()
                .filter(test -> Strings.similarity(input, test) >= 0.5d)
                .limit(limit)
                .collect(Collectors.toSet());
    }

    // ===

    @Nonnull
    public Set<CommandType> getCommands() {
        return Set.copyOf(this.commands.values());
    }

    // ===

    @Nonnull
    public Optional<CommandType> find(@Nonnull String key) {
        CommandType type = this.commands.get(key);
        if(type == null) return Optional.empty();
        return Optional.of(type);
    }

    @Nonnull
    public Optional<CommandType> findByAlias(@Nonnull String alias) {
        String key = this.aliases.get(alias);
        if(key == null) return Optional.empty();
        return find(key);
    }

}
