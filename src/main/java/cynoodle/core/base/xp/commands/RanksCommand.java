/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp.commands;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.Strings;
import cynoodle.core.api.text.Options;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.xp.Rank;
import cynoodle.core.base.xp.RankManager;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to display a table of all available {@link Rank Ranks} on the context guild.
 */
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

    {
        this.getOptionsBuilder()
                .add(OPT_XP);
    }

    //

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        RankManager manager = module.getRanks();
        //

        boolean displayXP = input.hasOption(OPT_XP);

        // ===

        List<Rank> ranks = manager.all(context.getGuildPointer())
                .sorted()
                .collect(Collectors.toList());

        if(ranks.isEmpty()) {
            context.queueReply("**|** There are currently no Ranks on this server.");
            return;
        }

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

        context.queueReply(out.toString());
    }
}
