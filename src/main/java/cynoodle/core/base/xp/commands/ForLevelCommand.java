/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp.commands;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.xp.XPFormula;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:xp:forlevel")
@CAliases({"forlevel","flevel","flvl","forlvl","forl"})
public final class ForLevelCommand extends Command {
    private ForLevelCommand() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        int value = input.requireParameterAs(0, "Level value", PrimitiveParsers.parseInteger());

        XPFormula formula = module.getFormula();

        //

        long xp = formula.getRequiredXP(value);

        //

        context.queueReply("**|** To reach Level `" + Numbers.format(value) + "` you require at least **`" + Numbers.format(xp) + "` XP**.");
    }
}
