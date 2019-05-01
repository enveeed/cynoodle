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

package cynoodle.base.xp.commands;

import cynoodle.api.Numbers;
import cynoodle.api.parser.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.XPFormula;
import cynoodle.base.xp.XPModule;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:xp:forxp")
@CAliases({"forxp","fxp","fx"})
public final class ForXPCommand extends Command {
    private ForXPCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        long value = input.requireParameterAs(0, "XP value", PrimitiveParsers.parseLong());

        XPFormula formula = module.getFormula();

        //

        int level = formula.getReachedLevel(value);

        //

        context.queueReply("**|** With `" + Numbers.format(value) + "` XP you can reach **Level `" + Numbers.format(level) + "`**.");
    }
}
