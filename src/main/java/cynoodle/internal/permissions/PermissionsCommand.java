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

package cynoodle.test.permissions;

import cynoodle.test.commands.*;
import cynoodle.test.commands.CommandAliases;
import cynoodle.test.commands.CommandKey;
import cynoodle.test.commands.CommandOption;
import cynoodle.test.commands.CommandPermission;
import cynoodle.util.options.Option;

import javax.annotation.Nonnull;

/**
 * General permission management command.
 */
@CommandKey(PermissionsModule.IDENTIFIER + ":permissions")
@CommandPermission("permissions.permissions")
@CommandAliases({"permissions", "perms"})
public final class PermissionsCommand implements Command {
    private PermissionsCommand() {}

    // ===

    @CommandOption
    private final static Option OPT_TEST = Option.newFlagOption("flag", 'f');

    @Override
    public void execute(@Nonnull Context context) throws Exception {

        Input input = context.input();



    }
}
