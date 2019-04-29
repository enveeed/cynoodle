/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Static utility for colors.
 */
public final class Colors {
    private Colors() {}

    // ===

    @Nonnull
    public static Color averageByImage(@Nonnull BufferedImage image) {

        long sumr = 0, sumg = 0, sumb = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }

        int num = image.getWidth() * image.getHeight();

        return new Color(
                (int) sumr / num,
                (int) sumg / num,
                (int) sumb / num);

    }
}
