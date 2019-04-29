/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.profiles;

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.Members;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
