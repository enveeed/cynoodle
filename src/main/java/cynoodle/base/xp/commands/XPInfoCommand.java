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

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.XPModule;
import cynoodle.base.xp.XPSettings;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

/**
 * Command to display general XP info for the context guild.
 */
@CIdentifier("base:xp:info")
@CAliases({"xpinfo","xinfo","xpi","xi"})
public final class XPInfoCommand extends Command {
    private XPInfoCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        XPSettings settings = module.getSettings(context.getGuildPointer());

        //

        StringBuilder out = new StringBuilder();

        out.append("**XP Information**").append("\n\n");

        out.append("You can gain from `")
                .append(settings.getGainMin())
                .append("` up to `")
                .append(settings.getGainMax())
                .append("` XP per message, with a timeout of `")
                .append(settings.getGainTimeout().toSeconds())
                .append("` seconds.");

        //

        context.queueReply(out.toString());
    }
}
