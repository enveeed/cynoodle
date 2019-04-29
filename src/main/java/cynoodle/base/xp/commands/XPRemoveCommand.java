/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp.commands;

import cynoodle.api.Numbers;
import cynoodle.api.parser.PrimitiveParsers;
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
