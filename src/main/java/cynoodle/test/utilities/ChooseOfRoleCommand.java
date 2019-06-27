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

package cynoodle.test.utilities;

import cynoodle.util.Random;
import cynoodle.util.options.Option;
import cynoodle.util.parsing.PrimitiveParsers;
import cynoodle.test.commands.*;
import cynoodle.test.local.LocalContext;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.discord.Roles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.List;

@CIdentifier("base:utilities:choose_of_role")
@CAliases({"chooseof"})
public final class ChooseOfRoleCommand extends Command {
    private ChooseOfRoleCommand() {}

    private static final Option OPT_COUNTDOWN = Option.newValueOption("countdown",'c');

    // ===

    {
        this.getOptionsBuilder()
                .add(OPT_COUNTDOWN);
    }

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        DiscordPointer roleP = input.requireParameterAs(0, "role", Roles.parserAt(context.getGuildPointer()));
        Role role = roleP.asRole()
                .orElseThrow(() -> CommandErrors.simple("Unknown Role `" + roleP.getID() + "`!"));

        //

        List<Member> members = context.getGuild()
                .getMembersWithRoles(role);

        if(members.size() == 0)
            throw CommandErrors.simple("Nobody has the Role **" + role.getName()+"**!");

        //

        if(input.hasOption(OPT_COUNTDOWN)) {

            int seconds = input.getOptionValueAs(OPT_COUNTDOWN, PrimitiveParsers.parseInteger());
            if(seconds > 20) throw CommandErrors.simple("Countdown cannot be over `20` seconds!");
            if(seconds < 3) throw CommandErrors.simple("Countdown must be at least `3` seconds!");

            context.queueReply("Choosing one member out of **" + role.getName() + "** in **`" + seconds
                    + "` seconds** ... (`" + members.size() + "` members)");

            int count = seconds;
            while (count > 0) {
                context.queueReply(Integer.toString(count));
                Thread.sleep(1000);
                count--;
            }
        }
        else context.queueReply("Choosing one member out of **" + role.getName() + "** ... (`" + members.size() + "` members)");

        //

        Member member = Random.nextOf(members);

        //

        context.queueReply("**" + Members.formatOf(context).format(DiscordPointer.to(member.getUser())) + "** was chosen!");
    }
}
