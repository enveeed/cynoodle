/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.utilities;

import cynoodle.api.Random;
import cynoodle.api.text.Options;
import cynoodle.api.parser.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
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

    private static final Options.Option OPT_COUNTDOWN = Options.newValueOption("countdown",'c');

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
