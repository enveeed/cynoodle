/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
