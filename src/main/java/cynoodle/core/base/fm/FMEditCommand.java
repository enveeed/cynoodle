/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.input.Options;
import cynoodle.core.api.input.Parameters;
import cynoodle.core.api.text.BooleanParser;
import cynoodle.core.api.text.StringParser;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

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
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        UEntityManager<FM> fmManager = module.getFMManager();

        FM fm = fmManager.firstOrCreate(context.getUser());

        //

        Parameters parameters = input.getParameters();

        boolean reset = input.hasOption(OPT_RESET);

        String selector = parameters.getAs(0, StringParser.get())
                .orElseThrow();

        //

        if(selector.equals("name")) {

            if(reset) {
                fm.setUsername(FM.DEF_USERNAME);
                context.getChannel().sendMessage("**|** last.fm username was reset.").queue();
            }
            else {
                String username = parameters.getAs(1, StringParser.get())
                        .orElseThrow(() -> new CommandException("last.fm username must be given!"));

                fm.setUsername(username);

                context.getChannel().sendMessage("**|** last.fm username was set to `" + username + "`.").queue();
            }

            fm.persist();

            return;
        }
        else if(selector.equals("format")) {

            throw new CommandException("TODO");

        }
        else if(selector.equals("profile")) {

            if(reset) {
                fm.setProfileEnabled(FM.DEF_PROFILE_ENABLED);
                context.getChannel().sendMessage("**|** last.fm profile connection was reset.").queue();
            }
            else {
                boolean profileEnabled = parameters.getAs(1, BooleanParser.get())
                        .orElseThrow();

                fm.setProfileEnabled(profileEnabled);

                if(profileEnabled)
                    context.getChannel().sendMessage("**|** last.fm profile connection was enabled.").queue();
                else
                    context.getChannel().sendMessage("**|** last.fm profile connection was disabled.").queue();
            }

            fm.persist();

            return;
        }
        else throw new CommandException(); // TODO exception
    }
}
