/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.core.base.commands.CommandErrors.simple;

@CIdentifier("base:xp:transfer")
@CAliases({"xpt","xt","transferxp","xptrans","transx"})
public final class XPTransferCommand extends Command {
    private XPTransferCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        DiscordPointer memberFrom = input.requireParameterAs(0, "member from", Members.parserOf(context));
        DiscordPointer memberTo = input.requireParameterAs(1, "member to", Members.parserOf(context));
        long value = input.requireParameterAs(2, "value", PrimitiveParsers.parseLong());

        // ===

        User userFrom = memberFrom.asUser()
                .orElseThrow(() -> simple("There is no User for the 'from' Member!"));

        User userTo = memberTo.asUser()
                .orElseThrow(() -> simple("There is no User for the 'to' Member!"));

        XP xpFrom = xpManager.first(XP.filterMember(DiscordPointer.to(context.getGuild()), memberFrom))
                .orElseThrow(() -> simple("There is no XP for the 'from' Member!"));

        XP xpTo = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), memberTo));

        // validation

        if(userFrom.isBot() || userTo.isBot()) throw simple("Bots can not have XP.");
        if(value <= 0) throw simple("You can not transfer a negative or zero amount of XP!");
        if(value > xpFrom.get()) throw simple("You can not transfer more XP than the source Member has!");

        xpFrom.remove(value);
        xpTo.add(value);

        xpFrom.persist();
        xpTo.persist();

        //

        context.getChannel().sendMessage(
                String.format("  **|** Successfully transferred `%s` XP from **%s** to **%s**.",
                        Numbers.format(value),
                        Members.formatOf(context).format(memberFrom),
                        Members.formatOf(context).format(memberTo)
                )).queue();

    }
}
