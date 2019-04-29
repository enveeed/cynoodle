/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
