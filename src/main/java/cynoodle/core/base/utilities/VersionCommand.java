/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.utilities;

import cynoodle.core.BuildConfig;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.DiscordModule;
import cynoodle.core.module.Module;
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
