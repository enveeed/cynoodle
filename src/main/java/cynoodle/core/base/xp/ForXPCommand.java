/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:xp:forxp")
@CAliases({"forxp","fxp","fx"})
public final class ForXPCommand extends Command {
    private ForXPCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        long value = input.requireParameterAs(0, "XP value", PrimitiveParsers.parseLong());

        XPFormula formula = module.getFormula();

        //

        int level = formula.getReachedLevel(value);

        //

        context.queueReply("**|** With `" + Numbers.format(value) + "` XP you can reach **Level `" + Numbers.format(level) + "`**.");
    }
}
