/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.IntegerParser;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cynoodle.core.base.command.CommandExceptions.missingParameter;
import static cynoodle.core.base.command.CommandExceptions.simple;

@CIdentifier("base:strikes:remove")
@CAliases({"strikeremove","strike-","str-","strr"})
public final class StrikeRemoveCommand extends Command {
    private StrikeRemoveCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter("member"));
        int index = parameters.get(1)
                .map(IntegerParser.get()::parse)
                .orElseThrow();

        //

        List<Strike> strikes = manager
                .stream(Strike.filterMember(DiscordPointer.to(context.getGuild()), member))
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

        out.append(new StrikeFormatter().format(strike));

        context.getChannel().sendMessage(out.toString()).queue();
    }
}
