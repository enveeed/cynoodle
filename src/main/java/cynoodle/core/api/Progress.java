/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;

/**
 * Represents a progress as a value between 0 and 1.
 */
public final class Progress {

    private double value;

    // ===

    private Progress(double value) {
        if(value > 1d || value < 0d) throw new IllegalArgumentException();
        this.value = value;
    }

    // ===

    public double get() {
        return this.value;
    }

    public double getAsPercent() {
        return this.value * 100d;
    }

    // ===

    @Nonnull
    public static Progress of(double value) {
        if(value > 1d || value < 0d) throw new IllegalArgumentException();
        return new Progress(value);
    }

    @Nonnull
    public static Progress of(long of, long max) {
        if(max < of || of < 0) throw new IllegalArgumentException();
        double value = (double) of / max;
        return of(value);
    }
}
