/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.makeme;

import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.Optional;

@CIdentifier("base:makeme:unmakeme")
@CAliases({"unmakeme","umm"})
public final class UnMakeMeCommand extends Command {
    private UnMakeMeCommand() {}

    private final MakeMeModule module = Module.get(MakeMeModule.class);

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        String key = input.requireParameter(0, "key");

        // check for make-me

        Optional<MakeMe> mmResult = module.controller().onGuild(context.getGuildPointer())
                .find(key);

        if(mmResult.isPresent()) {

            MakeMe mm = mmResult.orElseThrow();

            module.controller().onMember(context.getGuildPointer(), context.getUserPointer())
                    .unmake(mm);

            context.queueReply("**" + mm.getName() + "** was removed from you!");
            return;
        }

        // check for make-me groups

        Optional<MakeMeGroup> groupResult = module.controller().onGuild(context.getGuildPointer())
                .findGroup(key);

        if(groupResult.isPresent()) {

            MakeMeGroup group = groupResult.orElseThrow();

            module.controller().onMember(context.getGuildPointer(), context.getUserPointer())
                    .unmake(group);

            context.queueReply("All make-me from group **" + group.getName() + "** were removed from you!");
            return;
        }

        // nothing found

        throw CommandErrors.simple("Could not find a make-me or make-me group for key `" + key + "`!");
    }
}
