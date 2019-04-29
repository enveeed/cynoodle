/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.fm;

import cynoodle.api.Strings;
import cynoodle.base.commands.CommandContext;
import de.umass.lastfm.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;

/**
 * Equal to {@link SimpleFMFormat}, except that the cover is displayed as a huge image.
 */
final class SimpleCoverFMFormat implements FMFormat {
    SimpleCoverFMFormat() {}

    // ===

    @Nonnull
    @Override
    public MessageEmbed createEmbed(@Nonnull CommandContext context, @Nonnull Track track) {

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

        if(url.isPresent()) {
            eOut.setImage(url.orElseThrow());
        }

        // === COLOR ===

        Optional<Color> color = FMUtil.findColor(track);

        if(color.isPresent()) eOut.setColor(color.orElseThrow());

        //

        return eOut.build();

    }
}
