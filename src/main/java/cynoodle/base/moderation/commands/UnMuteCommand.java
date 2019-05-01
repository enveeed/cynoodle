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
import cynoodle.base.moderation.ModerationController;
import cynoodle.base.moderation.ModerationModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:moderation:unmute")
@CAliases("unmute")
public final class UnMuteCommand extends Command {
    private UnMuteCommand() {}

    private final ModerationController controller = Module.get(ModerationModule.class)
            .controller();

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer user =
                input.requireParameterAs(0, "user", Members.parserOf(context));

        //

        ModerationController.OnMember onMember = controller.onMember(context.getGuildPointer(), user);

        // ensure mute state so we can safely say member is not muted if that's the case
        onMember.applyMute(false);

        if(!onMember.isMuted()) throw CommandErrors.simple("Member is not muted!");

        //

        onMember.unmute();

        context.queueReply("**|** **" + Members.formatOf(context).format(user) + "** was unmuted.");
    }
}
