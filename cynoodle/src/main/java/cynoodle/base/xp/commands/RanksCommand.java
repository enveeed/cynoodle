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
import cynoodle.util.Strings;
import cynoodle.module.Module;
import cynoodle.util.options.Option;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.Rank;
import cynoodle.base.xp.RankManager;
import cynoodle.base.xp.XPModule;

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
    private final static Option OPT_XP = Option.newFlagOption("xp", 'x');

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
