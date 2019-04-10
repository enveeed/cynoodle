/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.utilities;

import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;

import javax.annotation.Nonnull;

@CIdentifier("base:utilities:help_temporary")
@CAliases({"help","h","?","commands","commandlist"})
// TODO this is only temporary
public final class TemporaryHelpCommand extends Command {
    private TemporaryHelpCommand() {}

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        StringBuilder out = new StringBuilder();

        out.append("__**Commands**__\n\n");
        out.append("*This is a temporary `!help` command.*\n");

        out.append("\n**last.fm**\n");
        out.append("`!fm (format)` **|** ").append("Display your current or last track on last.fm, optionally with format").append("\n");
        out.append("`!fmedit (property) (value)` **|** ").append("View or edit your last.fm settings").append("\n");

        out.append("\n**XP**\n");
        out.append("`!xp` or `!rank` **|** ").append("View your current XP and Rank").append("\n");
        out.append("`!lb` **|** ").append("View the leaderboard").append("\n");
        out.append("`!xpi` **|** ").append("View the current XP gain on the server").append("\n");
        out.append("`!forlevel (level)` **|** ").append("Calculate the required XP for a given level").append("\n");
        out.append("`!forxp (xp)` **|** ").append("Calculate the reached level for a given XP amount").append("\n");
        out.append("`!ranks (--xp)` **|** ").append("View a list of all available ranks, optionally with required XP").append("\n");
        out.append("`!xp+ (member) (amount)` **|** ").append("Add XP to a member").append("\n");
        out.append("`!xp- (member) (amount)` **|** ").append("Remove XP from a member").append("\n");
        out.append("`!xpt (member from) (member to) (amount)` **|** ").append("Transfer XP between members").append("\n");

        out.append("\n**Moderation**\n");
        out.append("`!strikes (member) (--all)` **|** ").append("View strikes, optionally all").append("\n");
        out.append("`!strike+ (member) (\"reason\") (decay day amount | never)` **|** ").append("Add a strike").append("\n");
        out.append("`!strike- (member) (index)` **|** ").append("Remove a strike").append("\n");
        out.append("`!strikerestore (member) (index)` **|** ").append("Restore a strike").append("\n");
        out.append("`!strikeedit (member) (reason (\"new reason\") | decay (decay day amount | never))` **|** ").append("Edit a strike").append("\n");
        out.append("`!strikeview (member) (index)` **|** ").append("View a strike in detail").append("\n");
        out.append("`!mute (member) (amount of days)` **|** ").append("Mute a member infinitely or with a time").append("\n");
        out.append("`!unmute (member)` **|** ").append("Unmute a member").append("\n");

        out.append("\n**Profiles**\n");
        out.append("`!p (member)` **|** ").append("View your profile or someone elses").append("\n");
        out.append("`!pedit (property) (value) (--reset)` **|** ").append("Edit your profile").append("\n");

        out.append("\n**Make-Me**\n");
        out.append("`!mm (key)` **|** ").append("View all make-me or assign yourself a make-me").append("\n");
        out.append("`!umm (key | group key)` **|** ").append("Remove a make-me or all make-me of a group from yourself").append("\n");

        out.append("\n**Utilities**\n");
        out.append("`!chooseof (role) (-c seconds)` **|** ").append("Choose a member of a role, optionally with a countdown").append("\n");
        out.append("`!version` **|** ").append("View bot version").append("\n");
        out.append("`!help` **|** ").append("View this help sheet").append("\n");

        context.queueReply(out.toString());
    }
}
