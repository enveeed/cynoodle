/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;

public final class Strings {
    private Strings() {}

    // === BOX ===

    @Nonnull
    public static String box(@Nonnull String text, int length, @Nonnull BoxAlignment alignment) {

        if(text.length() > length) text = ellipsis(text, length);

        int offset;

        if(alignment == BoxAlignment.LEFT) {
            offset = 0;
        }
        else if(alignment == BoxAlignment.CENTER) {
            offset = (length - text.length()) / 2;
        }
        else if(alignment == BoxAlignment.RIGHT) {
            offset = length - text.length();
        }
        else throw new IllegalArgumentException("Illegal alignment: " + alignment);

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int t = i - offset;
            if(t >= 0 && t < text.length()) out.append(text.charAt(t));
            else out.append(' ');
        }

        return out.toString();
    }

    @Nonnull
    public static String box(@Nonnull String text, int length) {
        return box(text, length, BoxAlignment.LEFT);
    }

    //

    public enum BoxAlignment {
        LEFT,
        CENTER,
        RIGHT,
        ;
    }

    // === ELLIPSIS ===

    @Nonnull
    public static String ellipsis(@Nonnull String input, int limit) {
        if(input.length()==0) return input;
        if (input.length() <= limit) {
            return input;
        }
        try {
            return input.substring(0, limit - 3) + "...";
        } catch (Exception e) {
            return input;
        }
    }


}
