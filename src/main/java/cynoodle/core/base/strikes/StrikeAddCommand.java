/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Instant;

import static cynoodle.core.base.command.CommandErrors.*;

@CIdentifier("base:strikes:add")
@CAliases({"strike","strikeadd", "strike+", "str+","stra"})
public final class StrikeAddCommand extends Command {
    private StrikeAddCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        StrikeSettings settings = module.getSettings().firstOrCreate(context.getGuild());
        StrikeManager manager = module.getStrikes();

        Parameters parameters = input.getParameters();

        DiscordPointer member   = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElseThrow(() -> missingParameter(this, "member"));
        String reason           = parameters.get(1)
                .orElseThrow(() -> missingParameter(this, "reason"));
        Decay decay             = parameters.get(2)
                .map(DecayParser.get()::parse)
                .orElse(settings.getDefaultDecay());
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
