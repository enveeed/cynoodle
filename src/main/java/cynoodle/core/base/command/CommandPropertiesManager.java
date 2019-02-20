/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

public final class CommandPropertiesManager extends GEntityManager<CommandProperties> {

    private final CommandModule module = Module.get(CommandModule.class);

    // ===

    CommandPropertiesManager() {
        super(CommandModule.TYPE_PROPERTIES);
    }

    // ===

    @Nonnull
    public CommandProperties firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull String identifier) {

        // require the descriptor for creation
        Command command = module.getRegistry().get(identifier).orElseThrow();
        CommandDescriptor descriptor = command.getDescriptor();

        return this.firstOrCreate(guild, CommandProperties.filterIdentifier(identifier), e -> e.create(descriptor));
    }
}
