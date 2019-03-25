/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;


import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:moderation:unmute")
@CAliases("unmute")
public final class UnMuteCommand extends Command {
    private UnMuteCommand() {}

    private final ModerationController controller = Module.get(ModerationModule.class)
            .controller();

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer member = input.requireParameterAs(0, "member", Members.parserOf(context));

        //

        ModerationController.OnMember onMember = controller.onMember(context.getGuildPointer(), member);

        // ensure mute state so we can safely say member is not muted if that's the case
        onMember.applyMute(false);

        if(!onMember.isMuted()) throw CommandErrors.simple(this, "Member is not muted!");

        //

        onMember.unmute();

        context.queueReply("**|** **" + Members.formatOf(context).format(member) + "** was unmuted.");
    }
}
