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

@CIdentifier("base:xp:add")
@CAliases({"xp+","x+","addxp","xpadd","addx"})
public final class XPAddCommand extends Command {
    private XPAddCommand() {}

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

        XP xp = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), member));

        // validation

        if(user.isBot()) throw simple("Bots can not have XP.");
        if(value <= 0) throw simple("You can not add a negative or zero amount of XP!");

        xp.add(value);
        xp.persist();

        //

        context.getChannel().sendMessage(
                String.format("  **|** Successfully added `%s` XP to **%s**.",
                        Numbers.format(value),
                        Members.formatOf(context).format(member)
                )).queue();
    }
}
