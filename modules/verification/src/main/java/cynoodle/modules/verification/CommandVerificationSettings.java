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

package cynoodle.modules.verification;

import cynoodle.base.commands.*;
import cynoodle.discord.format.DFFixedTable;

import javax.annotation.Nonnull;

@CommandKey(VerificationModule.IDENTIFIER + ":settings")
@CommandAliases({"vrfsettings","vrfset","vrfs"})
@CommandPermission("verification.command.settings")
public final class CommandVerificationSettings implements Command {
    private CommandVerificationSettings() {}

    // !vrfsettings reaction :upvote:
    // !vrfapply
    // !vrfstatus enveeed

    @Override
    public void execute(@Nonnull Context context) throws CommandException {

        Verification verification = Verification.get();

        Input input = context.getInput();

        if(!input.hasParameter(0)) {

            VerificationSettings settings = verification.getSettings(context.getGuild());

            StringBuilder out = new StringBuilder();

            out.append("**Verification**").append("\n\n");

            DFFixedTable.Builder builder = DFFixedTable.newBuilder(3, 4,
                    20, 30, 0);
            builder.setFreeLastColumn(true);

            ReactionE

            // view current settings

            builder.setRow(0, "role",
                    settings.getRole().map(x -> String.valueOf(x.getID())).orElse("-"),
                    "**|** The Role which all unverified Members should have");
            builder.setRow(1, "channel",
                    settings.getChannel().map(x -> String.valueOf(x.getID())).orElse("-"),
                    "**|** The channel which contains the verification reaction message");
            builder.setRow(2, "message",
                    settings.getMessage().stream().mapToObj(String::valueOf).findFirst().orElse("-"),
                    "**|** The message which contains the verification reaction");
            builder.setRow(3, "reaction",
                    settings.getReaction().orElse("-"),
                    "**|** The unicode emoji or local emote used as the reaction");

            out.append(builder.build().format());

            // check for errors or configuration warnings

            out.append("\n\n");

            if(settings.getRole().isEmpty()) {
                out.append(":warning: Role is not set.\n");
            }
            else {
                if(settings.getRole().get().getRole().isEmpty()) {
                    out.append(":warning: Configured Role does not exist.\n");
                }
            }
            if(settings.getChannel().isEmpty()) {
                out.append(":warning: Channel is not set.\n");
            }
            else {
                if(settings.getChannel().get().getTextChannel().isEmpty()) {
                    out.append(":warning: Configured Channel does not exist.\n");
                }
            }
            if(settings.getMessage().isEmpty()) {
                out.append(":warning: Message is not set.\n");
            }
            else {

            }
            if(settings.getReaction().isEmpty()) {
                out.append(":warning: Reaction is not set.\n");
            }

            return;
        }

    }
}
