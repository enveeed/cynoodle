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

import javax.annotation.Nonnull;

/**
 * A special {@link Exception} which can be thrown by {@link Command#execute(Context)}
 * which will be handled separately and which contains a {@link CommandError}.
 */
public final class CommandException extends RuntimeException {

    private final CommandError error;

    // ===

    public CommandException(@Nonnull CommandError error) {
        this.error = error;
    }

    // ===

    @Nonnull
    public CommandError getError() {
        return this.error;
    }

    // ===

    public static void throwError(@Nonnull CommandError error) throws CommandException {
        throw new CommandException(error);
    }
}
