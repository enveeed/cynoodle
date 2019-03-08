/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.PrimitiveParsers;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

import static cynoodle.core.base.command.CommandErrors.missingParameter;
import static cynoodle.core.base.command.CommandErrors.simple;

@CIdentifier("base:fm:edit")
@CAliases({"fmedit","fme"})
public final class FMEditCommand extends Command {
    private FMEditCommand() {}

    private final FMModule module = Module.get(FMModule.class);

    //

    private final static Options.Option OPT_RESET = Options.newFlagOption("reset", 'r');

    //

    @Override
    protected void onInit() {
        super.options.addOptions(
                OPT_RESET
        );
    }

    //

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        UEntityManager<FM> fmManager = module.getFMManager();

        FM fm = fmManager.firstOrCreate(context.getUser());

        //

        Parameters parameters = input.getParameters();

        boolean reset = input.hasOption(OPT_RESET);

        String selector = parameters.get(0)
                .orElseThrow(() -> missingParameter("selector"));

        //

        if(selector.equals("name")) {

            if(reset) {
                fm.setUsername(FM.DEF_USERNAME);
                context.getChannel().sendMessage("**|** last.fm username was reset.").queue();
            }
            else {
                String username = parameters.get(1)
                        .orElseThrow(() -> missingParameter("username"));

                fm.setUsername(username);

                context.getChannel().sendMessage("**|** last.fm username was set to `" + username + "`.").queue();
            }

            fm.persist();

            return;
        }
        else if(selector.equals("format")) {

            throw simple("TODO"); // TODO implementation

        }
        else if(selector.equals("profile")) {

            if(reset) {
                fm.setProfileEnabled(FM.DEF_PROFILE_ENABLED);
                context.getChannel().sendMessage("**|** last.fm profile connection was reset.").queue();
            }
            else {
                boolean profileEnabled = parameters.get(1)
                        .map(PrimitiveParsers.parseBoolean())
                        .orElseThrow(() -> missingParameter("on / off"));

                fm.setProfileEnabled(profileEnabled);

                if(profileEnabled)
                    context.getChannel().sendMessage("**|** last.fm profile connection was enabled.").queue();
                else
                    context.getChannel().sendMessage("**|** last.fm profile connection was disabled.").queue();
            }

            fm.persist();

            return;
        }
        else throw simple("TODO"); // TODO exception
    }
}
