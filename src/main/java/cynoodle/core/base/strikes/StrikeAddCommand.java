/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.input.Options;
import cynoodle.core.api.input.Parameters;
import cynoodle.core.api.text.StringParser;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MParser;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Instant;

@CIdentifier("base:strikes:add")
@CAliases({"strike","strikeadd", "strike+", "str+","stra"})
public final class StrikeAddCommand extends Command {
    private StrikeAddCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        StrikeSettings settings = module.getSettings().firstOrCreate(context.getGuild());
        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member   = parameters.getAs(0, MParser.create(context)).orElseThrow();
        String reason           = parameters.getAs(1, StringParser.get()).orElseThrow();
        Decay decay             = parameters.getAs(2, DecayParser.get()).orElse(settings.getEffectiveDefaultDecay());
        Instant timestamp       = Instant.now(); // TODO timestamp from parameters

        //

        Strike strike = manager.create(DiscordPointer.to(context.getGuild()), member, reason);

        strike.setTimestamp(timestamp);
        strike.setDecay(decay);

        strike.persist();

        //

        StringBuilder out = new StringBuilder();

        out.append("**|** Added new strike.");
        out.append("\n");

        out.append(new StrikeFormatter().format(strike));

        //

        context.getChannel().sendMessage(out.toString()).queue();
    }

}
