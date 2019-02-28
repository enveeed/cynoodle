/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.LongParser;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

@CIdentifier("base:xp:transfer")
@CAliases({"xpt","xt","transferxp","xptrans","transx"})
public final class XPTransferCommand extends Command {
    private XPTransferCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        Parameters parameters = input.getParameters();

        DiscordPointer memberFrom = parameters.getAs(0, Members.parserOf(context))
                .orElseThrow();
        DiscordPointer memberTo = parameters.getAs(1, Members.parserOf(context))
                .orElseThrow();
        long value = parameters.getAs(2, LongParser.get())
                .orElseThrow();

        // ===

        User userFrom = memberFrom.asUser()
                .orElseThrow(() -> new CommandException("There is no User for the 'from' Member!"));

        User userTo = memberTo.asUser()
                .orElseThrow(() -> new CommandException("There is no User for the 'to' Member!"));

        XP xpFrom = xpManager.first(XP.filterMember(DiscordPointer.to(context.getGuild()), memberFrom))
                .orElseThrow(() -> new CommandException("There is no XP for the 'from' Member!"));

        XP xpTo = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), memberTo));

        // validation

        if(userFrom.isBot() || userTo.isBot()) throw new CommandException("Bots can not have XP.");
        if(value <= 0) throw new CommandException("You can not transfer a negative or zero amount of XP!");
        if(value > xpFrom.get()) throw new CommandException("You can not transfer more XP than the source Member has!");

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
