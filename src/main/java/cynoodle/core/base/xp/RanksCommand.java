/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.primitives.Longs;
import com.mongodb.client.model.Filters;
import cynoodle.core.api.Numbers;
import cynoodle.core.api.Strings;
import cynoodle.core.api.text.Options;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cynoodle.core.base.command.CommandErrors.*;

@CIdentifier("base:xp:ranks")
@CAliases("ranks")
public final class RanksCommand extends Command {
    private RanksCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    /**
     * Flag to enable the display of the required XP for the Rank level.
     */
    private final static Options.Option OPT_XP = Options.newFlagOption("xp", 'x');

    //

    @Override
    protected void onInit() {
        this.options.addOptions(OPT_XP);
    }

    //

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        RankManager rankManager = module.getRankManager();

        boolean displayXP = input.hasOption(OPT_XP);

        // ===

        List<Rank> ranks = rankManager.stream(Filters.and(Rank.filterGuild(context.getGuild())))
                .sorted((o1, o2) -> Longs.compare(o1.getLevel(), o2.getLevel()))
                .collect(Collectors.toList());

        if(ranks.isEmpty()) throw simple("There are no Ranks.");

        //

        StringBuilder out = new StringBuilder();

        out.append("**__Ranks__**\n\n");

        int levelWidth = 7;
        int xpWidth = 13;

        out.append("**`\u200b").append(Strings.box(" Level", levelWidth)).append("\u200b`** ");

        if(displayXP) out.append("**`\u200b")
                .append(Strings.box("XP ", xpWidth, Strings.BoxAlignment.RIGHT))
                .append("\u200b`**");

        out.append("\n");

        for (Rank rank : ranks) {

            out.append("`\u200b").append(Strings.box(" " + rank.getLevel(), levelWidth)).append("\u200b` ");

            if(displayXP) out.append("`\u200b")
                    .append(Strings.box(Numbers.format(rank.getRequiredXP()) + " ", xpWidth, Strings.BoxAlignment.RIGHT))
                    .append("\u200b` ");

            out.append(" ").append(rank.getName()).append("\n");
        }

        //

        context.getChannel().sendMessage(out.toString()).queue();
    }
}
