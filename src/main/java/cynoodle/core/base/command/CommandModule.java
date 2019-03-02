/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.google.common.eventbus.Subscribe;
import com.google.common.flogger.FluentLogger;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
@MIdentifier("base:command")
@MRequires("base:localization")
public final class CommandModule extends Module {
    private CommandModule() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    final static EntityType<CommandSettings> TYPE_SETTINGS = EntityType.of(CommandSettings.class);
    final static EntityType<CommandProperties> TYPE_PROPERTIES = EntityType.of(CommandProperties.class);

    //

    private final CommandRegistry registry = new CommandRegistry();

    private CommandSettingsManager settings;
    private CommandPropertiesManager properties;

    private CommandHandler handler;

    private CommandPool pool;

    private CommandMappingsManager mappingsManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.settings = new CommandSettingsManager();
        this.properties = new CommandPropertiesManager();

        this.handler = new CommandHandler();
        this.pool = new CommandPool();

        this.mappingsManager = new CommandMappingsManager();

        this.registry.register(TestCommand.class);

        //

        this.settings.ensureIndexes();
        this.properties.ensureIndexes();
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        LOG.atInfo().log("Shutting down command pool ...");

        this.pool.shutdown();
    }

    // ===

    @Nonnull
    public CommandRegistry getRegistry() {
        return this.registry;
    }

    //

    @Nonnull
    public CommandSettingsManager getSettings() {
        return this.settings;
    }

    @Nonnull
    public CommandPropertiesManager getProperties() {
        return this.properties;
    }

    // ===

    @Nonnull
    CommandHandler getHandler() {
        return this.handler;
    }

    @Nonnull
    CommandPool getPool() {
        return this.pool;
    }

    // ===

    @Nonnull
    public CommandMappingsManager getMappingsManager() {
        return this.mappingsManager;
    }

    // ===

    @Subscribe
    private void handle(@Nonnull DiscordEvent e) {
        if(e.is(GuildMessageReceivedEvent.class)) {
            GuildMessageReceivedEvent event = e.get(GuildMessageReceivedEvent.class);
            handler.handle(event);
        }
    }
}
