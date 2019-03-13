/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.text.PrimitiveParsers;
import cynoodle.core.base.command.*;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandErrors.simple;

@CIdentifier("base:xp:add")
@CAliases({"xp+","x+","addxp","xpadd","addx"})
public final class XPAddCommand extends Command {
    private XPAddCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalizationContext local) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();

        DiscordPointer member = input.requireParameterAs(0, "member", Members.parserOf(context)::parse);
        long value = input.requireParameterAs(1, "value", PrimitiveParsers.parseLong());

        // ===

        User user = member.asUser()
                .orElseThrow(() -> simple(this, "There is no User for the given Member!"));

        XP xp = xpManager.firstOrCreate(XP.filterMember(DiscordPointer.to(context.getGuild()), member));

        // validation

        if(user.isBot()) throw simple(this, "Bots can not have XP.");
        if(value <= 0) throw simple(this, "You can not add a negative or zero amount of XP!");

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
