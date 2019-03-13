/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.base.command.*;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Instant;

@CIdentifier("base:strikes:add")
@CAliases({"strike","strikeadd", "strike+", "str+","stra"})
public final class StrikeAddCommand extends Command {
    private StrikeAddCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalizationContext local) throws Exception {

        StrikeSettings settings = module.getSettings().firstOrCreate(context.getGuild());
        StrikeManager manager = module.getStrikes();

        DiscordPointer member = input.requireParameterAs(0, "member", Members.parserOf(context)::parse);
        String reason = input.requireParameter(1, "reason");
        Decay decay = input.getParameterAs(2, "decay", DecayParser.get()::parse).orElse(settings.getDefaultDecay());
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
