/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.PrimitiveParsers;
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

import static cynoodle.core.base.command.CommandErrors.missingParameter;
import static cynoodle.core.base.command.CommandErrors.simple;

@CIdentifier("base:strikes:edit")
@CAliases({"strikeedit","strikee","stre"})
public final class StrikeEditCommand extends Command {
    private StrikeEditCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter(this, "member"));
        int index = parameters.get(1)
                .map(PrimitiveParsers.parseInteger())
                .orElseThrow(() -> missingParameter(this, "index"));
        String selector = parameters.get(2)
                .orElseThrow(() -> missingParameter(this, "selector"));

        //

        List<Strike> strikes = manager
                .stream(Strike.filterMember(DiscordPointer.to(context.getGuild()), member))
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw simple(this, "There is no strike at index `" + index + "`.");

        Strike strike = strikes.get(index);

        //

        StringBuilder out = new StringBuilder();

        if(selector.equalsIgnoreCase("reason")) {

            String reason = parameters.get(3)
                    .orElseThrow(() -> missingParameter(this, "reason"));

            strike.setReason(reason);
            strike.persist();

            out.append("**|** Strike reason was set.");
        }
        else if(selector.equalsIgnoreCase("decay")) {

            Decay decay = parameters.get(3)
                    .map(DecayParser.get()::parse)
                    .orElseThrow(() -> missingParameter(this,"decay"));

            strike.setDecay(decay);
            strike.persist();

            out.append("**|** Strike decay was set.");

        }
        else if(selector.equalsIgnoreCase("time")) {
            // TODO edit timestamp
            throw simple(this,"TODO");

        }
        else {
            // TODO throw useful exception
            throw simple(this, "TODO");
        }

        //

        out.append("\n");

        out.append(new StrikeFormatter().format(strike));

        context.getChannel().sendMessage(out.toString()).queue();

    }

}
