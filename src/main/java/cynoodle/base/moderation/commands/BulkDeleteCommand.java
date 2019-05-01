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

package cynoodle.base.moderation.commands;

import cynoodle.api.parser.PrimitiveParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@CIdentifier("base:moderation:delete")
@CAliases({"delete","bulkdelete","del","bdel"})
public final class BulkDeleteCommand extends Command {
    private BulkDeleteCommand() {}

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        int amount = input.requireParameterAs(0, "amount", PrimitiveParsers.parseInteger());

        //

        MessageHistory history = context.getChannel()
                .getHistoryBefore(context.getMessage(), amount)
                .complete();

        ArrayList<Message> messages = new ArrayList<>(history.getRetrievedHistory());

        messages.add(context.getMessage()); // delete the command message too

        //

        context.getChannel()
                .deleteMessages(messages)
                .complete();
    }
}
