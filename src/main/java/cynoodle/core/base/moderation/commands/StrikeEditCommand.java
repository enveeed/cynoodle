/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation.commands;

import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.moderation.*;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cynoodle.core.base.commands.CommandErrors.simple;

@CIdentifier("base:moderation:strike_edit")
@CAliases({"strikeedit","strikee","stre"})
public final class StrikeEditCommand extends Command {
    private StrikeEditCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StrikeManager manager = module.getStrikeManager();

        //

        DiscordPointer user =
                input.requireParameterAs(0, "user", Members.parserOf(context));
        int index =
                input.requireParameterAs(1, "index", PrimitiveParsers.parseInteger());
        String selector =
                input.requireParameter(2, "selector");

        //

        List<Strike> strikes = manager.allOfMember(context.getGuildPointer(), user)
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw simple("There is no strike at index `" + index + "`.");

        Strike strike = strikes.get(index);

        //

        StringBuilder out = new StringBuilder();

        if(selector.equalsIgnoreCase("reason")) {

            String reason = input.requireParameter(3, "reason");

            strike.setReason(reason);
            strike.persist();

            out.append("**|** Strike reason was set.");
        }
        else if(selector.equalsIgnoreCase("decay")) {

            Decay decay = input.requireParameterAs(3, "decay", Decay.parser());

            strike.setDecay(decay);
            strike.persist();

            out.append("**|** Strike decay was set.");

        }
        else if(selector.equalsIgnoreCase("time")) {
            // TODO edit timestamp
            throw simple("timestamp editing is not supported yet");

        }
        else {
            // TODO throw useful exception
            throw simple("not sure what you want me to do, what do you want to edit?");
        }

        //

        out.append("\n");

        out.append(new StrikeFormatter().format(strike, local));

        context.getChannel().sendMessage(out.toString()).queue();

    }

}
