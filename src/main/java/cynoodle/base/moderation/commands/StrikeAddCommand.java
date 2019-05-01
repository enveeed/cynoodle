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

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.moderation.*;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.time.Instant;

@CIdentifier("base:moderation:strike_add")
@CAliases({"strike","strikeadd", "strike+", "str+","stra"})
public final class StrikeAddCommand extends Command {
    private StrikeAddCommand() {}

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StrikeSettings settings = module.getStrikeSettingsManager().forGuild(context.getGuildPointer());
        StrikeManager manager = module.getStrikeManager();

        DiscordPointer user =
                input.requireParameterAs(0, "user", Members.parserOf(context));
        String reason =
                input.requireParameter(1, "reason");
        Decay decay =
                input.getParameterAs(2, "decay", Decay.parser()).orElse(settings.getDefaultDecay());
        Instant timestamp =
                Instant.now(); // TODO timestamp from parameters

        //

        Strike strike = manager.create(context.getGuildPointer(), user, reason, decay, timestamp);

        //

        StringBuilder out = new StringBuilder();

        out.append("**|** Added new strike.");
        out.append("\n");

        out.append(new StrikeFormatter().format(strike, local));

        //

        context.getChannel().sendMessage(out.toString()).queue();
    }

}
