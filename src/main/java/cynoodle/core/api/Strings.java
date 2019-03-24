/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;
import java.util.Locale;

public final class Strings {
    private Strings() {}

    public static final char NON_BREAKING_WHITESPACE = '\u00A0';
    public static final char ZERO_WIDTH_WHITESPACE = '\u200b';

    // === BOX ===

    @Nonnull
    public static String box(@Nonnull String text, int length, @Nonnull BoxAlignment alignment, char whitespace) {

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
            else out.append(whitespace);
        }

        return out.toString();
    }

    @Nonnull
    public static String box(@Nonnull String text, int length) {
        return box(text, length, BoxAlignment.LEFT, ' ');
    }

    @Nonnull
    public static String box(@Nonnull String text, int length, @Nonnull BoxAlignment alignment) {
        return box(text, length, alignment, ' ');
    }

    @Nonnull
    public static String box(@Nonnull String text, int length, char whitespace) {
        return box(text, length, BoxAlignment.LEFT, whitespace);
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

    // === CHAIN ===

    @Nonnull
    public static String chain(@Nonnull String input, int length) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) out.append(input);
        return out.toString();
    }

    public static String chain(char input, int length) {
        return chain(String.valueOf(input), length);
    }

    // === SIMILARITY ===

    /**
     * Calculates the similarity between two strings as a value between 0 and 1,
     * using {@link #levenshteinDistance(String, String)}.
     * @param a first string
     * @param b second string
     * @param limit the limit for the distance (over which the value is always 0)
     * @return similarity value, between 0 and 1
     */
    public static double similarity(@Nonnull String a, @Nonnull String b, int limit) {

        // a should always be greater or equal
        if (a.length() < b.length()) {
            String longer = b;
            b = a;
            a = longer;
        }

        int al = a.length();

        if (al == 0) return 1.0;

        al = Math.min(al, limit);

        return (al - levenshteinDistance(a, b, limit)) / (double) al;

    }

    /**
     * Calculates the similarity between two strings as a value between 0 and 1,
     * using {@link #levenshteinDistance(String, String)}.
     * The limit is Integer.MAX_VALUE.
     * @param a first string
     * @param b second string
     * @return similarity value, between 0 and 1
     */
    public static double similarity(@Nonnull String a, @Nonnull String b) {
        return similarity(a, b, Integer.MAX_VALUE);
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     * Ideally, a should be greater or equal length than b. If
     * this is not the case both are switched.
     * @param a first string
     * @param b second string
     * @param limit the limit for the distance (after which the algorithm is aborted)
     * @return the Levenshtein distance
     */
    public static int levenshteinDistance(@Nonnull String a, @Nonnull String b, int limit) {

        a = a.toLowerCase(Locale.ENGLISH);
        b = b.toLowerCase(Locale.ENGLISH);

        // a should always be greater or equal
        if (a.length() < b.length()) {
            String longer = b;
            b = a;
            a = longer;
        }

        int[] costs = new int[b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (a.charAt(i - 1) != b.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        if(newValue > limit) return limit;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[b.length()] = lastValue;
        }
        return costs[b.length()];
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     * Ideally, a should be greater or equal length than b. If
     * this is not the case both are switched.
     * The limit is Integer.MAX_VALUE.
     * @param a first string
     * @param b second string
     * @return the Levenshtein distance
     */
    public static int levenshteinDistance(@Nonnull String a, @Nonnull String b) {
        return levenshteinDistance(a, b, Integer.MAX_VALUE);
    }
}
