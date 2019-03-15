/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Instant;

@CIdentifier("base:strikes:add")
@CAliases({"strike","strikeadd", "strike+", "str+","stra"})
public final class StrikeAddCommand extends Command {
    private StrikeAddCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StrikeSettings settings = module.getStrikeSettingsManager().firstOrCreate(context.getGuild());
        StrikeManager manager = module.getStrikeManager();

        DiscordPointer member = input.requireParameterAs(0, "member", Members.parserOf(context));
        String reason = input.requireParameter(1, "reason");
        Decay decay = input.getParameterAs(2, "decay", Decay.parser()).orElse(settings.getDefaultDecay());
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

        out.append(new StrikeFormatter().format(strike, local));

        //

        context.getChannel().sendMessage(out.toString()).queue();
    }

}
