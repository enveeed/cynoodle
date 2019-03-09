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
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandErrors.*;

@CIdentifier("base:xp:transfer")
@CAliases({"xpt","xt","transferxp","xptrans","transx"})
public final class XPTransferCommand extends Command {
    private XPTransferCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        Parameters parameters = input.getParameters();

        DiscordPointer memberFrom = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter(this, "member from"));
        DiscordPointer memberTo = parameters.get(1)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter(this, "member to"));
        long value = parameters.get(2)
                .map(LongParser.get()::parse)
                .orElseThrow(() -> missingParameter(this, "value"));

        // ===

        User userFrom = memberFrom.asUser()
                .orElseThrow(() -> simple(this, "There is no User for the 'from' Member!"));

        User userTo = memberTo.asUser()
                .orElseThrow(() -> simple(this, "There is no User for the 'to' Member!"));

        XP xpFrom = xpManager.first(XP.filterMember(DiscordPointer.to(context.getGuild()), memberFrom))
                .orElseThrow(() -> simple(this, "There is no XP for the 'from' Member!"));

        XP xpTo = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), memberTo));

        // validation

        if(userFrom.isBot() || userTo.isBot()) throw simple(this, "Bots can not have XP.");
        if(value <= 0) throw simple(this, "You can not transfer a negative or zero amount of XP!");
        if(value > xpFrom.get()) throw simple(this, "You can not transfer more XP than the source Member has!");

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
