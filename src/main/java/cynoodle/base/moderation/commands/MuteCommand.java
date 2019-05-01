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

import cynoodle.api.parser.TimeParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.moderation.ModerationController;
import cynoodle.base.moderation.ModerationModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.time.Duration;

@CIdentifier("base:moderation:mute")
@CAliases({"mute","stfu"})
public final class MuteCommand extends Command {
    private MuteCommand() {}

    private final ModerationController controller = Module.get(ModerationModule.class)
            .controller();

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer member = input.requireParameterAs(0, "member", Members.parserOf(context));

        Duration duration = null;
        if(input.hasParameter(1)) duration = input.requireParameterAs(1, "duration", TimeParsers.parseDuration());

        //

        ModerationController.OnMember onMember = controller.onMember(context.getGuildPointer(), member);

        if(onMember.isMuted()) throw CommandErrors.simple("Member is already muted!");

        if(duration == null) {
            onMember.muteInfinite();
            context.queueReply("**|** **" + Members.formatOf(context).format(member) + "** was muted infinitely.");
        }
        else {
            onMember.muteFinite(duration);
            context.queueReply("**|** **" + Members.formatOf(context).format(member) + "** was muted for `" + duration.toDays() + "` days.");
        }
    }
}
