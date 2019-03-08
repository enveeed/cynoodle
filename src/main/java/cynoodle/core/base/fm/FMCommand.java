/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.Strings;
import cynoodle.core.api.text.Options;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.command.CAliases;
import cynoodle.core.base.command.CIdentifier;
import cynoodle.core.base.command.Command;
import cynoodle.core.base.command.CommandContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;

import static cynoodle.core.base.command.CommandErrors.*;

@CIdentifier("base:fm:fm")
@CAliases({"fm","fmi","fminfo"})
public final class FMCommand extends Command {
    private FMCommand() {}

    private final FMModule module = Module.get(FMModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input) throws Exception {

        UEntityManager<FM> fmManager = module.getFMManager();

        //

        FM fm = fmManager.firstOrCreate(context.getUser());

        //

        String username = fm.getUsername()
                .orElseThrow(() -> simple("No username defined."));

        FMFormat format = fm.getPreferredFormat();

        // === API REQUEST ===

        PaginatedResult<Track> recent = User.getRecentTracks(username, 1, 1, module.getConfiguration().getAPIKey());

        if (recent.isEmpty()) throw simple("The last.fm API did not return any result.");

        Optional<Track> trackResult = recent.getPageResults().stream().findFirst();

        Track track = trackResult.orElseThrow(() -> simple("The last.fm API did not return any track."));

        // === DISPLAY ===

        MessageEmbed embed = createEmbed(track, format);

        context.getChannel().sendMessage(embed).queue();
    }

    // ===

    @Nonnull
    private static MessageEmbed createEmbed(@Nonnull Track track, @Nonnull FMFormat format) {
        if(format == FMFormat.SIMPLE) return createEmbedSimple(track);
        else throw new IllegalStateException("Unknown format!");
    }

    //

    @Nonnull
    private static MessageEmbed createEmbedSimple(@Nonnull Track track) {

        EmbedBuilder eOut = new EmbedBuilder();

        // === META ===

        int spacing = 70;

        String description = String.format("**%s**\n\n%s\n%s",
                Strings.box(track.getName(), spacing, Strings.NON_BREAKING_WHITESPACE),
                Strings.box(track.getArtist(), spacing, Strings.NON_BREAKING_WHITESPACE),
                Strings.box(track.getAlbum(), spacing, Strings.NON_BREAKING_WHITESPACE));

        eOut.setDescription(description);

        // === IMAGE ===

        Optional<String> url = FMUtil.findImageLargest(track);

        if(url.isPresent()) eOut.setThumbnail(url.orElseThrow());

        // === COLOR ===

        Optional<Color> color = FMUtil.findColor(track);

        if(color.isPresent()) eOut.setColor(color.orElseThrow());

        //

        return eOut.build();
    }
}
