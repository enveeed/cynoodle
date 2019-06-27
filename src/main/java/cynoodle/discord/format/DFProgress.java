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

package cynoodle.discord.format;

import javax.annotation.Nonnull;

/**
 * Formatter for a progress bar.
 */
public final class DFProgress implements DFormattable {

    private final char char_empty;
    private final char char_full;
    private final int length;

    private final double value;

    // ===

    private DFProgress(char char_empty, char char_full, int length, double value) {
        this.char_empty = char_empty;
        this.char_full = char_full;
        this.length = length;
        this.value = value;
    }

    // ===

    public double getValue() {
        return this.value;
    }

    public double getValueAsPercent() {
        return this.value * 100d;
    }

    //

    public char getCharEmpty() {
        return this.char_empty;
    }

    public char getCharFull() {
        return this.char_full;
    }

    // ===

    @Nonnull
    @Override
    public String format() {

        double threshold = this.value * length;

        StringBuilder out = new StringBuilder();

        int i = 1;

        while (i <= length){
            if(threshold > i) out.append(this.char_full);
            else out.append(this.char_empty);
            i++;
        }

        return out.toString();
    }

    // ===

    @Nonnull
    public static DFProgress of(char char_empty, char char_full, int length, double fraction) {
        if(fraction > 1d)
            throw new IllegalArgumentException("Fraction cannot be larger than 1.");
        if(fraction < 0d)
            throw new IllegalArgumentException("Fraction cannot be larger than 0.");
        return new DFProgress(char_empty, char_full, length, fraction);
    }

    @Nonnull
    public static DFProgress of(char char_full, int length, double fraction) {
        return of(' ', char_full, length, fraction);
    }

    @Nonnull
    public static DFProgress of(int length, double fraction) {
        return of(' ', '█', length, fraction);
    }
}
