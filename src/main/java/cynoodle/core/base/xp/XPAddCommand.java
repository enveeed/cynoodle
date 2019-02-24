/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.input.Options;
import cynoodle.core.api.input.Parameters;
import cynoodle.core.api.text.LongParser;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

@CIdentifier("base:xp:add")
@CAliases({"xp+","x+","addxp","xpadd","addx"})
public final class XPAddCommand extends Command {
    private XPAddCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.getAs(0, Members.parserOf(context))
                .orElseThrow();
        long value = parameters.getAs(1, LongParser.get())
                .orElseThrow();

        // ===

        User user = member.asUser()
                .orElseThrow(() -> new CommandException("There is no User for the given Member!"));

        XP xp = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), member));

        // validation

        if(user.isBot()) throw new CommandException("Bots can not have XP.");
        if(value <= 0) throw new CommandException("You can not add a negative or zero amount of XP!");

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
