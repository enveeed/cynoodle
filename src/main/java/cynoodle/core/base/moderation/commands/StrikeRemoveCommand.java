/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation.commands;

import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.moderation.ModerationModule;
import cynoodle.core.base.moderation.Strike;
import cynoodle.core.base.moderation.StrikeFormatter;
import cynoodle.core.base.moderation.StrikeManager;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cynoodle.core.base.commands.CommandErrors.simple;

@CIdentifier("base:moderation:strike_remove")
@CAliases({"strikeremove","strike-","str-","strr"})
public final class StrikeRemoveCommand extends Command {
    private StrikeRemoveCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StrikeManager manager = module.getStrikeManager();

        //

        DiscordPointer user =
                input.requireParameterAs(0, "user", Members.parserOf(context));
        int index =
                input.requireParameterAs(1, "index", PrimitiveParsers.parseInteger());

        //

        List<Strike> strikes = manager.allOfMember(context.getGuildPointer(), user)
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw simple("There is no strike at index `" + index + "`.");

        //

        Strike strike = strikes.get(index);

        if(strike.isRemoved()) {
            context.getChannel().sendMessage("This strike is already removed.").queue();
            return;
        }

        strike.setRemoved(true);
        strike.persist();

        //

        StringBuilder out = new StringBuilder();

        out.append("**|** Strike was marked as removed.");
        out.append("\n");

        out.append(new StrikeFormatter().format(strike, local));

        context.getChannel().sendMessage(out.toString()).queue();
    }
}
