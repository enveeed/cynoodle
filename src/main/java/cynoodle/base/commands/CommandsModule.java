/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.commands;

import com.google.common.eventbus.Subscribe;
import com.google.common.flogger.FluentLogger;
import cynoodle.discord.DiscordEvent;
import cynoodle.entities.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

@SuppressWarnings("UnstableApiUsage")
@MIdentifier("base:commands")
@MRequires("base:local")
@MRequires("base:access")
public final class CommandsModule extends Module {
    private CommandsModule() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    final static EntityType<CommandSettings> TYPE_SETTINGS = EntityType.of(CommandSettings.class);

    //

    private final CommandRegistry registry = new CommandRegistry();

    private CommandSettingsManager settings;

    private CommandHandler handler;
    private CommandPool pool;
    private CommandMappingsManager mappingsManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.settings = new CommandSettingsManager();

        this.handler = new CommandHandler();
        this.pool = new CommandPool();
        this.mappingsManager = new CommandMappingsManager();

        //

        this.settings.ensureIndexes();
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
