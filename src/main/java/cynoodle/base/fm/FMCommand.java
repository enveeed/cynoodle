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

package cynoodle.base.fm;

import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.discord.UEntityManager;
import cynoodle.module.Module;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Optional;

import static cynoodle.base.commands.CommandErrors.simple;

@CIdentifier("base:fm:fm")
@CAliases({"fm","fmi","fminfo"})
public final class FMCommand extends Command {
    private FMCommand() {}

    private static final String FALLBACK_FORMAT = "simple";

    private final FMModule module = Module.get(FMModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        UEntityManager<FMPreferences> preferencesManager = module.getPreferencesManager();

        //

        FMPreferences preferences = preferencesManager.firstOrCreate(context.getUser());

        // USERNAME

        String username = preferences.getUsername()
                .orElseThrow(() -> simple("No username defined."));

        // FORMAT

        String preferredFormatName = preferences.getFormat()
                .orElse(FALLBACK_FORMAT);

        String formatName = input.getParameter(0)
                .orElse(preferredFormatName);

        //

        Optional<FMFormat> formatResult = module.getFormatRegistry()
                .find(formatName);

        FMFormat format = formatResult.orElseThrow(() -> simple("No such format: `" + formatName + "`"));

        // === API REQUEST ===

        PaginatedResult<Track> recent = User.getRecentTracks(username, 1, 1, module.getConfiguration().getAPIKey());

        if (recent.isEmpty()) throw simple("The last.fm API did not return any result.");

        Optional<Track> trackResult = recent.getPageResults().stream().findFirst();

        Track track = trackResult.orElseThrow(() -> simple("The last.fm API did not return any track."));

        // === DISPLAY ===

        MessageEmbed embed = format.createEmbed(context, track);

        context.getChannel().sendMessage(embed).queue();
    }
}
