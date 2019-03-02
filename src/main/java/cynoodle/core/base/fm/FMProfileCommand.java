/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.text.Options;
import cynoodle.core.base.command.*;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandExceptions.*;

@CIdentifier("base:fm:profile")
@CAliases({"fmprofile","fmp"})
public final class FMProfileCommand extends Command {
    private FMProfileCommand() {}

    private final FMModule module = Module.get(FMModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        UEntityManager<FM> fmManager = module.getFMManager();

        FM fm = fmManager.firstOrCreate(context.getUser());

        String username = fm.getUsername()
                .orElseThrow(() -> simple("No username defined."));

        //

        throw simple("This command is not available yet.");
    }
}
