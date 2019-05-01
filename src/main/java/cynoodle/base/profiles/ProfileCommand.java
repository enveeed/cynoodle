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
