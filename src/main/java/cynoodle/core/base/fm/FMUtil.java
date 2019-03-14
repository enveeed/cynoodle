/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.api.Colors;
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
