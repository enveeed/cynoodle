/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profile;

import cynoodle.core.base.command.*;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.annotation.Nonnull;

@CIdentifier("base:profile:profile")
@CAliases({"profile","p","pr"})
public final class ProfileCommand extends Command {
    private ProfileCommand() {}

    private final ProfileModule module = Module.get(ProfileModule.class);

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull CommandInput input) throws Exception {

        DiscordPointer user = input.getParameterAs(0, "user", Members.parserOf(context)::parse)
                .orElse(context.getUserPointer());

        //

        Profile profile = module.getProfileManager().firstOrCreate(user);

        MessageEmbed embed = profile.createEmbed(context.getGuildPointer());

        //

        context.getChannel().sendMessage(embed).queue();
    }
}
