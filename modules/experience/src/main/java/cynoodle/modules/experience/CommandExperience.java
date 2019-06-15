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

package cynoodle.modules.experience;

import cynoodle.base.commands.Command;
import cynoodle.base.commands.CommandException;
import cynoodle.base.commands.Context;
import cynoodle.base.commands.CommandAliases;
import cynoodle.base.commands.CommandKey;
import cynoodle.base.commands.CommandPermission;

import javax.annotation.Nonnull;

/**
 * This command shows general experience information for a member.
 */
@CommandKey("cynoodle:experience")
@CommandAliases({"experience", "xp", "rank", "r"})
@CommandPermission("experience.command.experience")
public final class CommandExperience implements Command {
    private CommandExperience() {}

    @Override
    public void execute(@Nonnull Context context) throws CommandException {

    }
}
