/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.output;

import cynoodle.core.api.Progress;

import javax.annotation.Nonnull;

/**
 * Formatter for {@link Progress Progresses}.
 */
public final class ProgressFormatter implements Formatter<Progress> {

    private long length = 20;

    // ===

    private ProgressFormatter() {}

    // ===

    @Nonnull
    @Override
    public String format(@Nonnull Progress input) {

        double threshold = input.get() * length;

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
