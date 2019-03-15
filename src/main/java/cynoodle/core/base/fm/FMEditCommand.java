/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.Strings;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

import java.util.Optional;

import static cynoodle.core.base.commands.CommandErrors.simple;

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
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        UEntityManager<FMPreferences> preferencesManager = module.getPreferencesManager();

        FMPreferences preferences = preferencesManager.firstOrCreate(context.getUser());

        //

        if(!input.hasParameter(0)) {

            StringBuilder out = new StringBuilder();

            out.append("**last.fm**")
                    .append("\n\n");

            Optional<String> usernameResult = preferences.getUsername();
            out.append(formatPropertyListing(
                    "name",
                    usernameResult.isPresent() ? usernameResult.orElseThrow() : " - ",
                    "last.fm username"
            ));

            FMFormat format = preferences.getPreferredFormat();
            out.append(formatPropertyListing(
                    "format",
                    format.getName(),
                    "preferred `!fm` format"
            ));

            boolean profileEnabled = preferences.isProfileEnabled();
            out.append(formatPropertyListing(
                    "profile",
                    profileEnabled ? "on" : "off",
                    "last.fm social link on profile"
            ));

            //

            out.append("\n")
                    .append("`!fmedit [property] (--reset) (value)`");

            context.queueReply(out.toString());

            return;
        }

        //

        boolean reset = input.hasOption(OPT_RESET);
        String selector = input.requireParameter(0, "property");

        //

        if(selector.equals("name")) {

            if(reset) {
                preferences.setUsername(FMPreferences.DEF_USERNAME);
                context.getChannel().sendMessage("**|** last.fm username was reset.").queue();
            }
            else {
                String username = input.requireParameter(1, "username");

                preferences.setUsername(username);

                context.getChannel().sendMessage("**|** last.fm username was set to `" + username + "`.").queue();
            }

            preferences.persist();

            return;
        }
        else if(selector.equals("format")) {

           if(reset) {
               preferences.setPreferredFormat(FMPreferences.DEF_PREFERRED_FORMAT);
               context.getChannel().sendMessage("**|** last.fm preferred format was reset.").queue();
           }
           else {

               String formatName = input.requireParameter(1, "format name");

               FMFormat format = null;
               for (FMFormat test : FMFormat.values()) {
                   if(test.getName().equals(formatName)) {
                       format = test;
                       break;
                   }
               }

               if(format == null) {
                   context.queueError(CommandErrors.simple(this, "Unknown format: `" + formatName + "`!"));
                   return;
               }

               preferences.setPreferredFormat(format);

               context.getChannel().sendMessage("**|** last.fm preferred format was set to `" + format.getName() + "`.").queue();
           }

            preferences.persist();

        }
        else if(selector.equals("profile")) {

            if(reset) {
                preferences.setProfileEnabled(FMPreferences.DEF_PROFILE_ENABLED);
                context.getChannel().sendMessage("**|** last.fm profile connection was reset.").queue();
            }
            else {
                boolean profileEnabled = input.requireParameterAs(1, "profile on / off", PrimitiveParsers.parseBoolean());

                preferences.setProfileEnabled(profileEnabled);

                if(profileEnabled)
                    context.getChannel().sendMessage("**|** last.fm profile connection was enabled.").queue();
                else
                    context.getChannel().sendMessage("**|** last.fm profile connection was disabled.").queue();
            }

            preferences.persist();

            return;
        }
        else throw simple(this, "TODO"); // TODO exception
    }

    // ===

    @Nonnull
    private static String formatPropertyListing(@Nonnull String property,
                                                @Nonnull String value,
                                                @Nonnull String description) {

        StringBuilder out = new StringBuilder();

        out.append("**`\u200b ").append(Strings.box(property, 10)).append("\u200b`** ")
                .append("`\u200b ")
                .append(Strings.box(value, 20))
                .append("\u200b`")
                .append(" **|** ")
                .append(description)
                .append("\n");

        return out.toString();
    }
}
