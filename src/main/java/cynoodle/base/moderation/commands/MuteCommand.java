/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
