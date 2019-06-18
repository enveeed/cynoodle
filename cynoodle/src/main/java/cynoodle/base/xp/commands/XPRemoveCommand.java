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

import cynoodle.util.Numbers;
import cynoodle.util.parsing.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.XPModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.base.commands.CommandErrors.simple;

/**
 * Command to remove XP from a member.
 */
@CIdentifier("base:xp:remove")
@CAliases({"xp-","x-","removexp","xpremove","removex"})
public final class XPRemoveCommand extends Command {
    private XPRemoveCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer member =
                input.requireParameterAs(0, "member", Members.parserOf(context));
        long value =
                input.requireParameterAs(1, "value", PrimitiveParsers.parseLong());

        // ===

        User user = member.asUser().orElseThrow(() -> simple("There is no known User for the given User!"));

        // validation
        if(user.isBot()) throw simple("Bots can not have XP.");
        if(value <= 0) throw simple("You can not remove a negative or zero amount of XP!");

        module.controller()
                .onMember(context.getGuildPointer(), member)
                .modify(-value, context.getChannelPointer());

        //

        context.getChannel().sendMessage(
                String.format("  **|** Successfully removed `%s` XP from **%s**.",
                        Numbers.format(value),
                        Members.formatOf(context).format(member)
                )).queue();
    }
}