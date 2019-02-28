/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.IntegerParser;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@CIdentifier("base:strikes:remove")
@CAliases({"strikeremove","strike-","str-","strr"})
public final class StrikeRemoveCommand extends Command {
    private StrikeRemoveCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.getAs(0, Members.parserOf(context)).orElseThrow();
        int index = parameters.getAs(1, IntegerParser.get()).orElseThrow();

        //

        List<Strike> strikes = manager
                .stream(Strike.filterMember(DiscordPointer.to(context.getGuild()), member))
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw new CommandException("There is no strike at index `" + index + "`.");

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
