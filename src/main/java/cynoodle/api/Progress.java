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

package cynoodle.api;

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
