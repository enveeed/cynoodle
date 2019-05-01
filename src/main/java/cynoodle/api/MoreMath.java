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

public final class MoreMath {
    private MoreMath() {}

    // The sum of the first m integers: 0 -> 0; 1,2,3,...
    public static long sumFirstIntegers(long m) {
        if(m < 0) throw new IllegalArgumentException("Argument cannot be negative: "+m);
        return (m*(m+1)) / 2;
    }

    // The sum of the first m squares: 0 -> 0; 1,2,3,...
    public static long sumFirstSquares(long m) {
        if(m < 0) throw new IllegalArgumentException("Argument cannot be negative: "+m);
        return (m*(m+1)*((2*m)+1)) / 6;
    }
}
