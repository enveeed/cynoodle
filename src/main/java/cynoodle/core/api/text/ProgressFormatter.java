/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;

/**
 * Formatter for progress bars.
 */
public final class ProgressFormatter {

    private long length = 20;

    // ===

    private ProgressFormatter() {}

    // ===

    @Nonnull
    public String format(double fraction) throws IllegalArgumentException {

        if(fraction < 0) throw new IllegalArgumentException("Progress fraction cannot be less than zero!");
        if(fraction > 1) throw new IllegalArgumentException("Progress fraction cannot be greater than one!");

        double threshold = fraction * length;

        StringBuilder out = new StringBuilder();

        int i = 1;

        while (i <= length){
            if(threshold > i) out.append("█");
            else out.append(" ");
            i++;
        }

        return out.toString();
    }

    // ===

    @Nonnull
    public ProgressFormatter setLength(long length) {
        this.length = length;
        return this;
    }

    // ===

    @Nonnull
    public static ProgressFormatter create() {
        return new ProgressFormatter();
    }
}
