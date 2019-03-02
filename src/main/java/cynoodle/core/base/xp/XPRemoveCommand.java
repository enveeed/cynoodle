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
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandExceptions.*;

@CIdentifier("base:xp:remove")
@CAliases({"xp-","x-","removexp","xpremove","removex"})
public final class XPRemoveCommand extends Command {
    private XPRemoveCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter("member"));
        long value = parameters.get(1)
                .map(LongParser.get()::parse)
                .orElseThrow(() -> missingParameter("value"));

        // ===

        User user = member.asUser()
                .orElseThrow(() -> simple("There is no User for the given Member!"));

        XP xp = xpManager.first(XP.filterMember(DiscordPointer.to(context.getGuild()), member))
                .orElseThrow(() -> simple("There is no XP for this Member!"));

        // validation

        if(user.isBot()) throw simple("Bots can not have XP.");
        if(value <= 0) throw simple("You can not remove a negative or zero amount of XP!");
        if(value > xp.get()) throw simple("You can not remove more XP than the Member has!");

        xp.remove(value);
        xp.persist();

        //

        context.getChannel().sendMessage(
                String.format("  **|** Successfully removed `%s` XP from **%s**.",
                        Numbers.format(value),
                        Members.formatOf(context).format(member)
                )).queue();
    }
}
