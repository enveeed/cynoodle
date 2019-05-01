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

package cynoodle.base.utilities;

import cynoodle.core.BuildConfig;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.discord.DiscordModule;
import cynoodle.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.annotation.Nonnull;
import java.time.LocalDate;

@CIdentifier("base:utilities:version")
@CAliases({"version","info","bot","v", "ver"})
public final class VersionCommand extends Command {
    private VersionCommand() {}

    @Override
    protected void run(@Nonnull CommandContext context,
                       @Nonnull CommandInput input,
                       @Nonnull LocalContext local) throws Exception {

        EmbedBuilder eOut = new EmbedBuilder();

        eOut.setTitle("cynoodle");

        String avatarURL = Module.get(DiscordModule.class)
                .getAPI().getShardById(0)
                .getSelfUser().getAvatarUrl();
        if(avatarURL != null) eOut.setThumbnail(avatarURL);

        StringBuilder descOut = new StringBuilder();

        descOut.append("**`").append(BuildConfig.VERSION).append("`**");

        descOut.append("\n\n");

        descOut.append("[GitHub](https://github.com/enveeed/cynoodle-core)").append(" | ")
                .append("[Changelog](https://github.com/enveeed/cynoodle-core/blob/master/CHANGELOG.md)").append(" | ")
                .append("[Report a Bug](https://github.com/enveeed/cynoodle-core/issues/)");

        eOut.setDescription(descOut);

        eOut.setFooter("(C) enveeed " + LocalDate.now().getYear(), null);

        //

        context.queueReply(eOut.build());
    }
}
