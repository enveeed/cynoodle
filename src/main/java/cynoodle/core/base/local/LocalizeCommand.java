/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.local;

import cynoodle.core.base.commands.*;
import cynoodle.core.module.Module;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

import javax.annotation.Nonnull;
import java.util.List;

@CIdentifier("base:local:localize")
@CAliases({"localize","local","l"})
public final class LocalizeCommand extends Command {
    private LocalizeCommand() {}

    private final LocalModule module = Module.get(LocalModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        LocalPreferences preferences = module.getPreferencesManager()
                .firstOrCreate(context.getUser());

        // query history

        MessageHistory history = context.getChannel()
                .getHistoryBefore(context.getMessage(), 15)
                .complete();

        List<Message> messages = history.getRetrievedHistory();

        // analyze messages

        // TODO

        throw CommandErrors.simple("This command is not supported yet.");
    }
}
