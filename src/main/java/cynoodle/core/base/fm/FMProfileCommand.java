/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.text.Options;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandErrors.*;

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
                .orElseThrow(() -> simple(this, "No username defined."));

        //

        throw simple(this, "This command is not available yet.");
    }
}
