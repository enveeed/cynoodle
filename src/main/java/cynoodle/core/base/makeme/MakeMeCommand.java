/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.makeme;

import cynoodle.core.api.Strings;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.module.Module;

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
                            .append(mm.getName())
                            .append("\n");
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
                            .append(mm.getName())
                            .append("\n");
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
                .orElseThrow(() -> CommandErrors.simple(this, "No such make-me: `" + key + "`"));

        MakeMeStatus status = module.getStatusManager().firstOrCreate(
                context.getGuildPointer(),
                context.getUserPointer());

        //

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
