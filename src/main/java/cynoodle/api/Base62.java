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

public final class Base62 {
    private Base62() {}

    // ===

    // 0-9A-Za-z
    public static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // ===

    @Nonnull
    public static String toBase62(long n) {
        byte[] digits = MoreMath.toDigits(n, 62);
        char[] characters = new char[digits.length];
        for (int i = 0; i < digits.length; i++) characters[i] = ALPHABET.charAt(digits[i]);
        return String.valueOf(characters);
    }

    public static long fromBase62(@Nonnull String in) {
        char[] characters = in.toCharArray();
        byte[] digits = new byte[characters.length];
        for (int i = 0; i < characters.length; i++) digits[i] = (byte) ALPHABET.indexOf(characters[i]);
        return MoreMath.fromDigits(digits, 62);
    }
}
