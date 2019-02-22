/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.input.Options;
import cynoodle.core.api.input.Parameters;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MNameFormatter;
import cynoodle.core.discord.MParser;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@CIdentifier("base:strikes:list")
@CAliases({"strikes","strikeslist","strikelist","strlist","strl"})
public final class StrikeListCommand extends Command {
    private StrikeListCommand() {}

    private final StrikesModule module = Module.get(StrikesModule.class);

    /**
     * Option which enables the display of removed or decayed strikes.
     */
    private final static Options.Option OPT_ALL = Options.newFlagOption("all", 'a');

    // ===

    @Override
    protected void onInit() {
        this.options.addOptions(OPT_ALL);
    }

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        Parameters parameters = input.getParameters();

        boolean displayAll = input.hasOption(OPT_ALL);

        DiscordPointer member = parameters.getAs(0, new MParser(context.getGuild())).orElseThrow();

        // ===

        StrikeManager manager = module.getStrikes();

        List<Strike> strikes = manager
                .stream(Strike.filterMember(DiscordPointer.to(context.getGuild()), member))
                .sorted()
                .collect(Collectors.toList());

        StringBuilder out = new StringBuilder();

        out.append("Strikes for **")
                .append(MNameFormatter.of(context.getGuild())
                        .setMode(MNameFormatter.Mode.USER_FULL)
                        .format(member))
                .append("**")
                .append("\n\n");

        int index = -1;
        int skipped = 0;
        int displayed = 0;

        for (Strike strike : strikes) {

            index++;

            // skip decayed or removed
            if(!displayAll && (strike.isDecayed() || strike.isRemoved())) {
                skipped++;
                continue;
            }

            displayed++;

            out.append("`").append(index).append("` ").append(strike.getReason());

            out.append(" - ");

            long days = Duration.between(strike.getTimestamp(), Instant.now()).toDays();

            if(days > 0) {
                out.append(days).append(" days ago");
            }
            else if(days < 0) {
                out.append("in ").append(Math.abs(days)).append(" days");
            }
            else out.append("today");

            if(strike.isDecayable()) {

                long daysDecay = Duration.between(Instant.now(), strike.getDecayAt().orElseThrow()).toDays();

                out.append(", ");

                if(daysDecay > 0) {
                    out.append("decays in ").append(daysDecay).append(" days");
                }
                else if(daysDecay < 0) {
                    out.append("decayed ").append(Math.abs(daysDecay)).append(" days ago");
                }
                else {
                    if(strike.isDecayed()) out.append("decayed today");
                    else out.append("decays today");
                }

            }

            if(strike.isRemoved()) {
                out.append(", removed");
            }

            out.append("\n");

        }

        if(displayed == 0) {
            if(!displayAll) out.append("No effective strikes.");
            else out.append("No strikes.");

        }

        out.append("\n");

        if(skipped > 0)
            out.append("(").append(skipped).append(" removed or decayed").append(")\n\n");

        // ===

        context.getChannel().sendMessage(out.toString()).queue();

    }


}
