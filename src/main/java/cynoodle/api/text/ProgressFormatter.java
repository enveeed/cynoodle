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

package cynoodle.api.text;

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
