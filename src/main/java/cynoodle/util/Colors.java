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

package cynoodle.util;

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
