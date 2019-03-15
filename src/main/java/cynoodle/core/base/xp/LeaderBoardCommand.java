/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.Strings;
import cynoodle.core.api.text.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static cynoodle.core.base.commands.CommandErrors.simple;

@CIdentifier("base:xp:lb")
@CAliases({"leaderboard","lb","toplist","tl","levels","lvls"})
public final class LeaderBoardCommand extends Command {
    private LeaderBoardCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        MEntityManager<XP> xpManager = module.getXPManager();
        GEntityManager<XPSettings> settingsManager = module.getSettingsManager();
        LeaderBoardManager leaderBoardManager = module.getLeaderBoardManager();

        //

        int from = input.getParameterAs(0, "from", PrimitiveParsers.parseInteger()).orElse(1);
        int to = input.getParameterAs(1, "to", PrimitiveParsers.parseInteger()).orElse((from - 1) + 25);

        //

        if(to <= from) {
            throw simple(this, "The maximum rank you entered can not be smaller or equal to the minimum rank.");
        }

        if((to - from) + 1 > 30) {
            throw simple(this, "You can't view more than `30` ranks at a time.");
        }

        //

        Optional<LeaderBoard> boardOpt = leaderBoardManager.get(context.getGuildPointer());

        LeaderBoard board;

        // TODO temporary
        if(boardOpt.isEmpty()) {
            context.getChannel().sendMessage("**|** Updating leader board, this may take a few moments ...").complete();
            board = leaderBoardManager.generate(context.getGuildPointer());
        }
        else board = boardOpt.orElseThrow();

        //

        List<LeaderBoard.Entry> entries = board.sub(from - 1, to - 1);

        if(entries.size() == 0)
            throw simple(this, "There is not enough data in the range from minimum rank `#"
                    +from+"` to maximum rank `#"+to+"`!");

        StringBuilder out = new StringBuilder();

        //out.append("**__Leader Board__**\n\n");
        out.append("Ranks `#").append(from).append("` to `#").append(to).append("`. ");

        long minutes = Duration.between(board.getTimestamp(), Instant.now()).toMinutes();

        if(minutes < 5) out.append("Updated just now.\n\n");
        else out.append("Updated `").append(minutes).append("` minutes ago.\n\n");

        //

        int widthRank   = 4;
        int widthXP     = 13;
        int widthLevel  = 7;

        out.append("**`\u200B")
                .append(Strings.box(" #", widthRank)).append("\u200B` `\u200B")
                .append(Strings.box("XP ", widthXP, Strings.BoxAlignment.RIGHT)).append("\u200B` `\u200B")
                .append(Strings.box(" Level", widthLevel)).append("\u200B`**\n");

        for (LeaderBoard.Entry entry : entries) {

            int level = module.getFormula().getReachedLevel(entry.getXP());

            out.append("`\u200B")
                    .append(Strings.box(" " + entry.getRank(), widthRank)).append("\u200B` `\u200B")
                    .append(Strings.box(Numbers.format(entry.getXP()) + " ", widthXP, Strings.BoxAlignment.RIGHT)).append("\u200B` `\u200B")
                    .append(Strings.box(" " + Numbers.format(level), widthLevel)).append("\u200B`  ")
                    .append(Members.formatAt(context.getGuildPointer()).format(entry.getMember())).append("\n");

        }

        context.getChannel().sendMessage(out.toString()).queue();
    }

}
