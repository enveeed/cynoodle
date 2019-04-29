/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
