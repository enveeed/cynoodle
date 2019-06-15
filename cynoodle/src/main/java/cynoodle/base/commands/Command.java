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

import cynoodle.util.options.Option;

import javax.annotation.Nonnull;

/**
 * An executable command.
 */
public interface Command {

    /**
     * Execute the command with the given context.
     * @param context the command context
     * @throws CommandException see apiNote
     * @apiNote if this throws any other {@link Exception} which is not {@link CommandException}
     * it will be handled as an internal error.
     */
    void execute(@Nonnull Context context) throws CommandException;

    // ===

    // TODO possibly move those somewhere else, this interface should be clean ...

    /**
     * A flag for the command to provide additional debug information.
     */
    Option OPT_DEBUG = Option.newFlagOption("debug", '#');

    /**
     * A flag for the command to localize the output format.
     */
    Option OPT_LOCALIZE = Option.newFlagOption("localize",'l');

    /**
     * If the test instance is also present on a guild, this will force this instance to execute the command instead.
     */
    Option OPT_IGNORE_TEST = Option.newFlagOption("ignore-test", null);
}
