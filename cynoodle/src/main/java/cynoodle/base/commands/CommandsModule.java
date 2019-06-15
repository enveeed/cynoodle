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

import cynoodle.module.MIdentifier;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@MIdentifier(CommandsModule.IDENTIFIER)
public final class CommandsModule extends Module {
    private CommandsModule() {}

    static final String IDENTIFIER = "base:commands";

    // ===

    private Commands commands = null;

    private CommandsEventHandler eventHandler = new CommandsEventHandler();

    // ===

    @Override
    protected void start() {
        super.start();

        // initialize commands instance
        this.commands = new Commands();

        // register event handler
        this.registerListener(eventHandler);

        // register manager command
        this.commands.register(CommandCommandManager.TYPE);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public Commands getCommands() {
        if(this.commands == null)
            throw new IllegalStateException("CommandsModule must be started before accessing permissions!");
        return this.commands;
    }
}
