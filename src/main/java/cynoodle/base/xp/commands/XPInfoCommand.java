/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp.commands;

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.xp.XPModule;
import cynoodle.base.xp.XPSettings;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

/**
 * Command to display general XP info for the context guild.
 */
@CIdentifier("base:xp:info")
@CAliases({"xpinfo","xinfo","xpi","xi"})
public final class XPInfoCommand extends Command {
    private XPInfoCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        XPSettings settings = module.getSettings(context.getGuildPointer());

        //

        StringBuilder out = new StringBuilder();

        out.append("**XP Information**").append("\n\n");

        out.append("You can gain from `")
                .append(settings.getGainMin())
                .append("` up to `")
                .append(settings.getGainMax())
                .append("` XP per message, with a timeout of `")
                .append(settings.getGainTimeout().toSeconds())
                .append("` seconds.");

        //

        context.queueReply(out.toString());
    }
}
