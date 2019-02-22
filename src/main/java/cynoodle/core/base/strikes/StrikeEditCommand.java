/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.input.Options;
import cynoodle.core.api.input.Parameters;
import cynoodle.core.api.text.IntegerParser;
import cynoodle.core.api.text.StringParser;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MParser;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@CIdentifier("base:strikes:edit")
@CAliases({"strikeedit","strikee","stre"})
public final class StrikeEditCommand extends Command {
    private StrikeEditCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.getAs(0, new MParser(context.getGuild()))
                .orElseThrow();
        int index = parameters.getAs(1, IntegerParser.get())
                .orElseThrow();
        String selector = parameters.getAs(2, StringParser.get())
                .orElseThrow();

        //

        List<Strike> strikes = manager
                .stream(Strike.filterMember(DiscordPointer.to(context.getGuild()), member))
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw new CommandException("There is no strike at index `" + index + "`.");

        Strike strike = strikes.get(index);

        //

        StringBuilder out = new StringBuilder();

        if(selector.equalsIgnoreCase("reason")) {

            String reason = parameters.getAs(3, StringParser.get())
                    .orElseThrow();

            strike.setReason(reason);
            strike.persist();

            out.append("**|** Strike reason was set.");
        }
        else if(selector.equalsIgnoreCase("decay")) {

            Decay decay = parameters.getAs(3, DecayParser.get())
                    .orElseThrow();

            strike.setDecay(decay);
            strike.persist();

            out.append("**|** Strike decay was set.");

        }
        else if(selector.equalsIgnoreCase("time")) {
            // TODO edit timestamp
            throw new CommandException("TODO");

        }
        else {
            // TODO throw useful exception
            throw new CommandException("TODO");
        }

        //

        out.append("\n");

        out.append(new StrikeFormatter().format(strike));

        context.getChannel().sendMessage(out.toString()).queue();

    }

}
