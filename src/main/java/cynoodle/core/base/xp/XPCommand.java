/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.ProgressFormatter;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.Optional;

import static cynoodle.core.base.command.CommandExceptions.internalError;
import static cynoodle.core.base.command.CommandExceptions.simple;

@CIdentifier("base:xp:xp")
@CAliases({"xp", "rank", "r"})
public final class XPCommand extends Command {
    private XPCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();
        GEntityManager<XPSettings> settingsManager = module.getSettingsManager();
        RankManager rankManager = module.getRankManager();

        Parameters parameters = input.getParameters();

        DiscordPointer member = parameters.get(0)
                .map(Members.parserOf(context)::parse)
                .orElse(DiscordPointer.to(context.getUser()));

        // ===

        XP xp = xpManager.first(XP.filterMember(DiscordPointer.to(context.getGuild()), member))
                .orElseThrow(() -> simple("There is no XP for this Member!"));

        XPSettings settings = settingsManager.first(XP.filterGuild(context.getGuild()))
                .orElseThrow(() -> internalError());

        // === XP DATA ===

        XPFormula formula = module.getFormula();

        long xp_current = xp.get();

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

        Optional<Rank> level_next_rank = rankManager.firstByLevel(context.getGuildPointer(), level_next);

        String level_next_rank_out;

        if(level_next_rank.isPresent())
            level_next_rank_out = String.format("**`%s %s`**", level_next, level_next_rank.orElseThrow().getName());
        else
            level_next_rank_out = String.format("`%s`", level_next);

        // === LEADER BOARD ===

        Optional<LeaderBoard> board = module.getLeaderBoardManager().get(context.getGuildPointer());

        String rank_current_out;

        if(board.isPresent())
            rank_current_out = String.format(" | `# %s\u200b`", board.orElseThrow().findByMember(member)
                            .map(entry -> Numbers.format(entry.getRank()))
                            .orElse("- "));
        else
            rank_current_out = "";

        // === FORMATTING ===

        String out = String.format("**%s:** `%s` XP | Level `%s` **>>** `\u200b%s\u200b` `%s %%` `%s` **>>** %s %s",
                Members.formatAt(context.getGuildPointer()).format(member),
                Numbers.format(xp_current),
                Numbers.format(level_current),
                ProgressFormatter.create().setLength(20).format(level_next_fraction),
                Numbers.format(level_next_percent, 0),
                Numbers.format(level_next_xp_left),
                level_next_rank_out,
                rank_current_out
                );

        //

        context.getChannel().sendMessage(out).queue();

    }

}
