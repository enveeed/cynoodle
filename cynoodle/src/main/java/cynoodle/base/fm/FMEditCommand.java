/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.fm;

import cynoodle.util.Strings;
import cynoodle.discord.UEntityManager;
import cynoodle.module.Module;
import cynoodle.util.options.Option;
import cynoodle.util.parsing.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;

import javax.annotation.Nonnull;
import java.util.Optional;

import static cynoodle.base.commands.CommandErrors.simple;

@CIdentifier("base:fm:edit")
@CAliases({"fmedit","fme"})
public final class FMEditCommand extends Command {
    private FMEditCommand() {}

    private final FMModule module = Module.get(FMModule.class);

    //

    private final static Option OPT_RESET = Option.newFlagOption("reset", 'r');

    //

    {
        this.getOptionsBuilder()
                .add(OPT_RESET);
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

            Optional<String> formatNameResult = preferences.getFormat();
            out.append(formatPropertyListing(
                    "format",
                    formatNameResult.isPresent() ? formatNameResult.orElseThrow() : " - ",
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
               preferences.setFormat(FMPreferences.DEF_FORMAT);
               context.getChannel().sendMessage("**|** last.fm preferred format was reset.").queue();
           }
           else {

               String formatName = input.requireParameter(1, "format name");

               Optional<FMFormat> format = module.getFormatRegistry().find(formatName);
               if(format.isEmpty()) {
                   context.queueError(CommandErrors.simple("Unknown format: `" + formatName + "`!"));
                   return;
               }

               preferences.setFormat(formatName);

               context.getChannel().sendMessage("**|** last.fm preferred format was set to `" + formatName + "`.").queue();
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
        else throw simple("TODO"); // TODO exception
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
