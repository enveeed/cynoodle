/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp.commands;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.base.xp.XPStatus;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.core.base.commands.CommandErrors.simple;

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
