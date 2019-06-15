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

import cynoodle.util.Strings;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CIdentifier("base:makeme:makeme")
@CAliases({"makeme","mm"})
public final class MakeMeCommand extends Command {
    private MakeMeCommand() {}

    private final static String SEPARATOR = Strings.chain(Strings.NON_BREAKING_WHITESPACE, 6);

    // ===

    private final MakeMeModule module = Module.get(MakeMeModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        if(!input.hasParameter(0)) {

            MakeMeController.OnGuild controller = module.controller().onGuild(context.getGuildPointer());
            MakeMeController.OnMember controllerMember = module.controller().onMember(context.getGuildPointer(), context.getUserPointer());

            List<MakeMeGroup> groups = controller.allGroups()
                    .sorted(Comparator.comparing(MakeMeGroup::getName))
                    .collect(Collectors.toList());

            StringBuilder out = new StringBuilder();

            //

            for (MakeMeGroup group : groups) {

                out.append("**").append(group.getName()).append("**")
                        .append(SEPARATOR)
                        .append("`").append(group.getKey()).append("`");

                if(group.isUniqueEnabled()) out.append(" **`U`**");

                out.append("\n\n");

                List<MakeMe> members = controller.allByGroup(group)
                        .sorted(Comparator.comparing(MakeMe::getName))
                        .collect(Collectors.toList());

                for (MakeMe mm : members) {

                    out.append("`\u200b ")
                            .append(Strings.box(mm.getKey(), 20))
                            .append(" \u200b` **|** ")
                            .append(mm.getName());

                    if(controllerMember.has(mm)) out.append(SEPARATOR)
                            .append(" **`\u200b ✓ \u200b`**");

                    out.append("\n");
                }

                out.append("\n");
            }

            List<MakeMe> otherMMs = controller.allByGroup(null)
                    .sorted(Comparator.comparing(MakeMe::getName))
                    .collect(Collectors.toList());

            if(otherMMs.size() > 0) {

                // only display "other" if there were groups
                if(groups.size() > 0) out.append("**Other**").append("\n\n");

                for (MakeMe mm : otherMMs) {

                    out.append("`\u200b ")
                            .append(Strings.box(mm.getKey(), 20))
                            .append(" \u200b` **|** ")
                            .append(mm.getName());

                    if(controllerMember.has(mm)) out.append(SEPARATOR)
                            .append(" **`\u200b ✓ \u200b`**");

                    out.append("\n");
                }
            }

            //

            if(out.length() == 0) {
                context.queueReply("*There are no make-me.*");
                return;
            }

            context.queueReply(out.toString());

            return;
        }

        // ===

        String key = input.requireParameter(0, "key");

        //

        MakeMe mm = module.controller().onGuild(context.getGuildPointer())
                .find(key)
                .orElseThrow(() -> CommandErrors.simple("No such make-me: `" + key + "`"));

        MakeMeStatus status = module.getStatusManager().firstOrCreate(
                context.getGuildPointer(),
                context.getUserPointer());

        //

        if(!mm.canAccess(context.getMember())) {
            context.queueError(CommandErrors.permissionInsufficient(
                    "You are now allowed to access this make-me!"));
            return;
        }

        if(status.has(mm)) {
            context.queueReply("You already have the make-me **" + mm.getName() + "**.");
            return;
        }

        module.controller()
                .onMember(context.getGuildPointer(), context.getUserPointer())
                .make(mm);

        //

        context.queueReply("You were assigned **" + mm.getName() + "**!");
    }
}
