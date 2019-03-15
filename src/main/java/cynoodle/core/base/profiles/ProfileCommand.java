/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profiles;

import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.annotation.Nonnull;

@CIdentifier("base:profile:profile")
@CAliases({"profile","p","pr"})
public final class ProfileCommand extends Command {
    private ProfileCommand() {}

    private final ProfilesModule module = Module.get(ProfilesModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        DiscordPointer user = input.getParameterAs(0, "user", Members.parserOf(context))
                .orElse(context.getUserPointer());

        //

        Profile profile = module.getProfileManager().firstOrCreate(user);

        MessageEmbed embed = profile.createEmbed(context.getGuildPointer());

        //

        context.getChannel().sendMessage(embed).queue();
    }
}
