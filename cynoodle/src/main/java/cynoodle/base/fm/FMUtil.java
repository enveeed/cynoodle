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

import cynoodle.util.Colors;
import de.umass.lastfm.ImageHolder;
import de.umass.lastfm.ImageSize;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Static utility for the last.fm API.
 */
public final class FMUtil {
    private FMUtil() {}

    /**
     * All last.fm image sizes, from smallest to largest.
     */
    private static ImageSize[] sizes = new ImageSize[] {
            ImageSize.SMALL,
            ImageSize.MEDIUM,
            ImageSize.LARGE,
            ImageSize.LARGESQUARE,
            ImageSize.HUGE,
            ImageSize.EXTRALARGE,
            ImageSize.MEGA,
            ImageSize.ORIGINAL
    };

    // ===

    @Nonnull
    public static Optional<String> findImageLargest(@Nonnull ImageHolder holder) {
        return findImage(holder, false);
    }

    @Nonnull
    public static Optional<String> findImageSmallest(@Nonnull ImageHolder holder) {
        return findImage(holder, true);
    }

    @Nonnull
    public static Optional<String> findImage(@Nonnull ImageHolder holder, boolean smallest) {

        if(smallest)
            for (int i = 0; i < sizes.length; i++) {
                String url = holder.getImageURL(sizes[i]);
                if(url != null && !url.isBlank()) return Optional.of(url);
            }
        else {
            for (int i = sizes.length - 1; i >= 0; i--) {
                String url = holder.getImageURL(sizes[i]);
                if(url != null && !url.isBlank()) return Optional.of(url);
            }
        }

        return Optional.empty();
    }

    @Nonnull
    public static Optional<Color> findColor(@Nonnull ImageHolder holder) {

        Optional<String> smallestImageURL = findImageSmallest(holder);

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
