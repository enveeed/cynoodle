/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.Colors;
import cynoodle.core.api.Strings;
import cynoodle.core.api.input.Options;
import cynoodle.core.base.command.*;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@CIdentifier("base:fm:fm")
@CAliases({"fm","fmi","fminfo"})
public final class FMCommand extends Command {
    private FMCommand() {}

    private final FMModule module = Module.get(FMModule.class);

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        UEntityManager<FM> fmManager = module.getFMManager();

        //

        FM fm = fmManager.firstOrCreate(context.getUser());

        //

        String username = fm.getUsername()
                .orElseThrow(() -> new CommandException("No username defined."));

        FMFormat format = fm.getPreferredFormat();

        // === API REQUEST ===

        // TODO getApiKey
        PaginatedResult<Track> recent = User.getRecentTracks(username, 1, 1, null);

        if (recent.isEmpty()) throw new CommandException("The last.fm API did not return any result.");

        Optional<Track> trackResult = recent.getPageResults().stream().findFirst();

        Track track = trackResult.orElseThrow(() -> new CommandException("The last.fm API did not return any track."));

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

        Optional<String> url = findImageLargest(track);

        if(url.isPresent()) eOut.setThumbnail(url.orElseThrow());

        // === COLOR ===

        Optional<Color> color = findColor(track);

        if(color.isPresent()) eOut.setColor(color.orElseThrow());

        //

        return eOut.build();
    }

    //

    @Nonnull
    private static Optional<String> findImageLargest(@Nonnull Track track) {
        return findImage(track, false);
    }

    @Nonnull
    private static Optional<String> findImageSmallest(@Nonnull Track track) {
        return findImage(track, true);
    }

    @Nonnull
    private static Optional<String> findImage(@Nonnull Track track, boolean smallest) {

        ImageSize[] sizes = new ImageSize[] {
                ImageSize.SMALL,
                ImageSize.MEDIUM,
                ImageSize.LARGE,
                ImageSize.LARGESQUARE,
                ImageSize.HUGE,
                ImageSize.EXTRALARGE,
                ImageSize.MEGA,
                ImageSize.ORIGINAL
        };

        if(smallest)
            for (int i = 0; i < sizes.length; i++) {
                String url = track.getImageURL(sizes[i]);
                if(url != null && !url.isBlank()) return Optional.of(url);
            }
        else {
            for (int i = sizes.length - 1; i >= 0; i--) {
                String url = track.getImageURL(sizes[i]);
                if(url != null && !url.isBlank()) return Optional.of(url);
            }
        }

        return Optional.empty();
    }

    @Nonnull
    private static Optional<Color> findColor(@Nonnull Track track) {

        Optional<String> smallestImageURL = findImage(track, true);

        if(smallestImageURL.isPresent()) {

            try {
                URL url = new URL(smallestImageURL.orElseThrow());
                BufferedImage image = ImageIO.read(url);

                return Optional.of(Colors.averageByImage(image));

            } catch (MalformedURLException e) {
                throw new RuntimeException("last.fm returned bad image URL!", e);
            } catch (IOException e) {
                throw new RuntimeException("last.fm returned bad image data!", e);
            }

        }
        else return Optional.empty();
    }
}
