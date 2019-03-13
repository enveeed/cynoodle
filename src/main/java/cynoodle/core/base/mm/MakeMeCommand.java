/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.mm;

import cynoodle.core.base.command.*;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:mm:mm")
@CAliases({"makeme","mm"})
public final class MakeMeCommand extends Command {
    private MakeMeCommand() {}

    private final MakeMeModule module = Module.get(MakeMeModule.class);

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input, @Nonnull LocalizationContext local) throws Exception {

        String key = input.requireParameter(0, "key");

        //

        MakeMe mm = module.controller().onGuild(context.getGuildPointer())
                .find(key)
                .orElseThrow(() -> CommandErrors.simple(this, "No such make-me: `" + key + "`"));

        //

        MakeMeStatus status = module.getStatusManager().firstOrCreate(
                context.getGuildPointer(),
                context.getUserPointer());

        //

        if(status.has(mm)) {
            context.queueReply("You already have the make-me **" + mm.getName() + "**.");
            return;
        }

        module.controller()
                .onMember(context.getGuildPointer(), context.getUserPointer())
                .make(mm);

        //

        context.queueReply("You were assigned **" + mm.getName() + "**!");
    }
}
