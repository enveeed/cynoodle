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

package cynoodle.test.local;

import cynoodle.test.commands.*;
import cynoodle.module.Module;
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
