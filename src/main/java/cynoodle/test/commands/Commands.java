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

import cynoodle.discord.GEntityManager;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

public final class Commands {
    Commands() {}

    // ===

    private final GEntityManager<CommandsSettings> settingsEntityManager
            = new GEntityManager<>(CommandsSettings.TYPE);

    private final CommandRegistry registry
            = new CommandRegistry();
    private final CommandHandler handler
            = new CommandHandler();

    // ===

    @Nonnull
    public CommandRegistry getRegistry() {
        return this.registry;
    }

    //

    @Nonnull
    CommandHandler getHandler() {
        return this.handler;
    }

    // ===

    // NOTE: This is just a short-cut to CommandRegistry.register()
    public void register(@Nonnull CommandType type) {
        this.registry.register(type);
    }

    // ===

    @Nonnull
    public CommandsSettings getSettings(@Nonnull Guild guild) {
        return this.settingsEntityManager.firstOrCreate(guild);
    }

    // ===

    @Nonnull
    public static Commands get() {
        return Module.get(CommandsModule.class).getCommands();
    }
}
