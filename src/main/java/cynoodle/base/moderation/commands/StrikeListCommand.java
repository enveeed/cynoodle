/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.moderation.commands;

import cynoodle.api.text.Options;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.moderation.ModerationModule;
import cynoodle.base.moderation.Strike;
import cynoodle.base.moderation.StrikeManager;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.MFormatter;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@CIdentifier("base:moderation:strike_list")
@CAliases({"strikes","strikeslist","strikelist","strlist","strl"})
public final class StrikeListCommand extends Command {
    private StrikeListCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    /**
     * Option which enables the display of removed or decayed strikes.
     */
    private final static Options.Option OPT_ALL = Options.newFlagOption("all", 'a');

    //

    {
        this.getOptionsBuilder()
                .add(OPT_ALL);
    }

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        boolean displayAll = input.hasOption(OPT_ALL);

        DiscordPointer user =
                input.getParameterAs(0, "user", Members.parserOf(context))
                        .orElse(context.getUserPointer());

        // ===

        StrikeManager manager = module.getStrikeManager();

        List<Strike> strikes = manager.allOfMember(context.getGuildPointer(), user)
                .sorted()
                .collect(Collectors.toList());

        StringBuilder out = new StringBuilder();

        out.append("Strikes for **")
                .append(Members.formatOf(context)
                        .withMode(MFormatter.Mode.USER_FULL)
                        .format(user))
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
