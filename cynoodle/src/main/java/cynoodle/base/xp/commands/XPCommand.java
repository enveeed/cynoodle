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

package cynoodle.base.xp.commands;

import cynoodle.util.Numbers;
import cynoodle.util.text.ProgressFormatter;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.*;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Display the current {@link XPStatus} of the context member, as well as their rank on
 * the context guilds {@link LeaderBoard}.
 */
@CIdentifier("base:xp:xp")
@CAliases({"xp", "rank", "r"})
public final class XPCommand extends Command {
    private XPCommand() {}

    private final XPModule module = XPModule.get();

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        RankManager ranks = module.getRanks();

        //

        DiscordPointer user = input.getParameterAs(0, "user", Members.parserOf(context))
                .orElse(context.getUserPointer());

        // ===

        XPStatus status         = module.getStatus(context.getGuildPointer(), user);
        XPSettings settings     = module.getSettings(context.getGuildPointer());

        // === XP DATA ===

        XPFormula formula = module.getFormula();

        long xp_current = status.getXP();

        int level_current = formula.getReachedLevel(xp_current);

        int level_next = level_current + 1;

        long level_current_xp = formula.getRequiredXP(level_current);
        long level_next_xp = formula.getRequiredXP(level_next);

        long level_next_xp_step = level_next_xp - level_current_xp;
        long level_next_xp_of = xp_current - level_current_xp;
        long level_next_xp_left = level_next_xp_step - level_next_xp_of;

        double level_next_fraction = (double) level_next_xp_of / level_next_xp_step;
        double level_next_percent = level_next_fraction * 100d;

        // === RANK ===

        Optional<Rank> level_next_rank = ranks.getAtLevel(context.getGuildPointer(), level_next);

        String level_next_rank_out;

        if(level_next_rank.isPresent())
            level_next_rank_out = String.format("**`%s %s`**", level_next, level_next_rank.orElseThrow().getName());
        else
            level_next_rank_out = String.format("`%s`", level_next);

        // === LEADER BOARD ===

        Optional<LeaderBoard> board = module.getLeaderBoardManager().get(context.getGuildPointer());

        String rank_current_out;

        if(board.isPresent())
            rank_current_out = String.format(" | `# %s\u200b`", board.orElseThrow().findByMember(user)
                            .map(entry -> Numbers.format(entry.getRank()))
                            .orElse("- "));
        else
            rank_current_out = "";

        // === FORMATTING ===

        String out = String.format("**%s:** `%s` XP | Level `%s` **>>** `\u200b%s\u200b` `%s %%` `%s` **>>** %s %s",
                Members.formatAt(context.getGuildPointer()).format(user),
                Numbers.format(xp_current),
                Numbers.format(level_current),
                ProgressFormatter.create().setLength(20).format(level_next_fraction),
                Numbers.format(level_next_percent, 0),
                Numbers.format(level_next_xp_left),
                level_next_rank_out,
                rank_current_out
                );

        //

        context.queueReply(out);

    }

}
