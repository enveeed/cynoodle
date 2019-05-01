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

package cynoodle.base.makeme;

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.util.Optional;

@CIdentifier("base:makeme:unmakeme")
@CAliases({"unmakeme","umm"})
public final class UnMakeMeCommand extends Command {
    private UnMakeMeCommand() {}

    private final MakeMeModule module = Module.get(MakeMeModule.class);

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        String key = input.requireParameter(0, "key");

        // check for make-me

        Optional<MakeMe> mmResult = module.controller().onGuild(context.getGuildPointer())
                .find(key);

        if(mmResult.isPresent()) {

            MakeMe mm = mmResult.orElseThrow();

            module.controller().onMember(context.getGuildPointer(), context.getUserPointer())
                    .unmake(mm);

            context.queueReply("**" + mm.getName() + "** was removed from you!");
            return;
        }

        // check for make-me groups

        Optional<MakeMeGroup> groupResult = module.controller().onGuild(context.getGuildPointer())
                .findGroup(key);

        if(groupResult.isPresent()) {

            MakeMeGroup group = groupResult.orElseThrow();

            module.controller().onMember(context.getGuildPointer(), context.getUserPointer())
                    .unmake(group);

            context.queueReply("All make-me from group **" + group.getName() + "** were removed from you!");
            return;
        }

        // nothing found

        throw CommandErrors.simple("Could not find a make-me or make-me group for key `" + key + "`!");
    }
}
