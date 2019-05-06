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
@MRequires("base:permissions")
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
