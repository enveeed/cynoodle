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

import cynoodle.util.Strings;
import cynoodle.base.commands.CommandContext;
import de.umass.lastfm.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;

/**
 * Simple minimalistic format, without any additional meta data or links.
 */
final class SimpleFMFormat implements FMFormat {
    SimpleFMFormat() {}

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
            eOut.setThumbnail(url.orElseThrow());
        }

        // === COLOR ===

        Optional<Color> color = FMUtil.findColor(track);

        if(color.isPresent()) eOut.setColor(color.orElseThrow());

        //

        return eOut.build();

    }
}
