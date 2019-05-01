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
import cynoodle.base.xp.XPModule;
import cynoodle.base.xp.XPStatus;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.base.commands.CommandErrors.simple;

/**
 * Command to transfer XP from a given member to another member.
 */
@CIdentifier("base:xp:transfer")
@CAliases({"xpt","xt","transferxp","xptrans","transx"})
public final class XPTransferCommand extends Command {
    private XPTransferCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer userPFrom =
                input.requireParameterAs(0, "member from", Members.parserOf(context));
        DiscordPointer userPTo =
                input.requireParameterAs(1, "member to", Members.parserOf(context));
        long value =
                input.requireParameterAs(2, "value", PrimitiveParsers.parseLong());

        // ===

        User userFrom = userPFrom.asUser().orElseThrow(() -> simple("There is no User for the 'from' User!"));
        User userTo = userPTo.asUser().orElseThrow(() -> simple("There is no User for the 'to' User!"));

        XPStatus statusFrom = module.getStatus(context.getGuildPointer(), userPFrom);
        XPStatus statusTo = module.getStatus(context.getGuildPointer(), userPTo);

        // validation

        if(userFrom.isBot() || userTo.isBot()) throw simple("Bots can not have XP.");
        if(value <= 0) throw simple("You can not transfer a negative or zero amount of XP!");
        if(value > statusFrom.getXP()) throw simple("You can not transfer more XP than the source Member has!");

        // TODO replace with controller.transfer() to allow rank apply and notifications etc.
        statusFrom.removeXP(value);
        statusTo.addXP(value);

        statusFrom.persist();
        statusTo.persist();

        //

        context.getChannel().sendMessage(
                String.format("  **|** Successfully transferred `%s` XP from **%s** to **%s**.",
                        Numbers.format(value),
                        Members.formatOf(context).format(userPFrom),
                        Members.formatOf(context).format(userPTo)
                )).queue();

    }
}
