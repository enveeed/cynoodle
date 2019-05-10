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

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MoreMathTest {

    @Test
    void testDigits() {

        long in = 82938283829L;

        byte[] digits10 = MoreMath.toDigits(in, 10);
        byte[] digits16 = MoreMath.toDigits(in, 16);
        byte[] digits32 = MoreMath.toDigits(in, 32);
        byte[] digits64 = MoreMath.toDigits(in, 64);
        byte[] digits127 = MoreMath.toDigits(in, 127);

        System.out.println(Arrays.toString(digits10));
        System.out.println(Arrays.toString(digits16));
        System.out.println(Arrays.toString(digits32));
        System.out.println(Arrays.toString(digits64));
        System.out.println(Arrays.toString(digits127));

        long out10 = MoreMath.fromDigits(digits10, 10);
        long out16 = MoreMath.fromDigits(digits16, 16);
        long out32 = MoreMath.fromDigits(digits32, 32);
        long out64 = MoreMath.fromDigits(digits64, 64);
        long out127 = MoreMath.fromDigits(digits127, 127);

        System.out.println(out10);
        System.out.println(out16);
        System.out.println(out32);
        System.out.println(out64);
        System.out.println(out127);

        assertEquals(in, out10);
        assertEquals(in, out16);
        assertEquals(in, out32);
        assertEquals(in, out64);
        assertEquals(in, out127);
    }
}