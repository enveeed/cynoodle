/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.Maps;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contains immutable mappings for a guilds commands,
 * collected from the {@link CommandProperties}.
 */
public final class CommandMapper {

    private final ImmutableMap<String, String> mappings;

    // ===

    private CommandMapper(@Nonnull Map<String, String> mappings) {
        this.mappings = Maps.immutable.ofAll(mappings);
    }

    // ===

    @Nonnull
    public Optional<String> find(@Nonnull String input) {
        return Optional.ofNullable(this.mappings.get(input));
    }

    // ===

    @Nonnull
    public static CommandMapper collect(@Nonnull DiscordPointer guild) {

        CommandModule module = Module.get(CommandModule.class);

        Map<String, String> mappings = new HashMap<>();

        CommandRegistry registry = module.getRegistry();
        CommandPropertiesManager properties = module.getProperties();

        for (Command command : registry.all()) {

            CommandDescriptor descriptor = command.getDescriptor();
            String identifier = descriptor.getIdentifier();

            //

            CommandProperties commandProperties = properties.firstOrCreate(guild,
                    CommandProperties.filterIdentifier(identifier), p -> p.create(descriptor));

            //

            Set<String> aliases = commandProperties.getAliases();

            for (String alias : aliases)
                mappings.put(alias, identifier);

        }

        return new CommandMapper(mappings);
    }
}
