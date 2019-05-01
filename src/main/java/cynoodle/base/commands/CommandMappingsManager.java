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
import cynoodle.discord.DiscordPointer;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Manager for {@link CommandMappings}.
 */
public final class CommandMappingsManager {
    CommandMappingsManager() {}

    // ===

    private final MutableLongObjectMap<CommandMappings> mappings = new LongObjectHashMap<>();

    // ===

    @Nonnull
    public Optional<CommandMappings> get(@Nonnull DiscordPointer guild) {
        if(this.mappings.containsKey(guild.getID())) {
            return Optional.of(this.mappings.get(guild.getID()));
        } else return Optional.empty();
    }

    // ===

    @Nonnull
    @CanIgnoreReturnValue
    public CommandMappings generate(@Nonnull DiscordPointer guild) {

        CommandMappings mappings = CommandMappings.collect(guild);

        this.mappings.put(guild.getID(), mappings);

        return mappings;
    }
}
