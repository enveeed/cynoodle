/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

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

        XPSettings settings = module.getSettingsManager().firstOrCreate(context.getGuild());

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
