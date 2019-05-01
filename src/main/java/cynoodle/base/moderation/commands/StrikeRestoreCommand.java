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

import cynoodle.api.parser.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.moderation.*;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cynoodle.base.commands.CommandErrors.simple;

@CIdentifier("base:moderation:strike_restore")
@CAliases({"strikerestore","strikere","strre","strre"})
public final class StrikeRestoreCommand extends Command {
    private StrikeRestoreCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StrikeManager manager = module.getStrikeManager();

        //

        DiscordPointer user =
                input.requireParameterAs(0, "user", Members.parserOf(context));
        int index =
                input.requireParameterAs(1, "index", PrimitiveParsers.parseInteger());

        //

        List<Strike> strikes = manager.allOfMember(context.getGuildPointer(), user)
                .sorted()
                .collect(Collectors.toList());

        if(index < 0 || index >= strikes.size())
            throw simple("There is no strike at index `" + index + "`.");

        //

        Strike strike = strikes.get(index);

        if(!strike.isRemoved()) {
            context.getChannel().sendMessage("This strike is not removed cannot be restored!").queue();
            return;
        }

        strike.setRemoved(false);
        strike.persist();

        //

        StringBuilder out = new StringBuilder();

        out.append("**|** Strike is no longer marked as removed.");
        out.append("\n");

        out.append(new StrikeFormatter().format(strike, local));

        context.getChannel().sendMessage(out.toString()).queue();
    }

}
