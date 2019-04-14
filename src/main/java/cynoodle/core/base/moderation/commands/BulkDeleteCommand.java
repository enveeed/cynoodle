/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation.commands;

import cynoodle.core.api.parser.PrimitiveParsers;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
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
