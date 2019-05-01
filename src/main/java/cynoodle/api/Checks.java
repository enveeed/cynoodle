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

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;

/**
 * Validity checks for single values.
 */
public final class Checks {
    private Checks() {}

    // === NULL ===

    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T notNull(T value, @Nonnull String name)
            throws NullPointerException {
        if(value == null) throw new NullPointerException(name + " cannot be null!");
        return value;
    }

    @Nonnull
    @CanIgnoreReturnValue
    public static <T> T notNull(T value)
            throws NullPointerException {
        return notNull(value, "value");
    }

    // === STRINGS ===

    @Nonnull
    @CanIgnoreReturnValue
    public static String notEmpty(String value, @Nonnull String name)
            throws NullPointerException, IllegalArgumentException {
        Checks.notNull(value, name);
        if(value.isEmpty()) throw new IllegalArgumentException(name + " cannot be empty!");
        return value;
    }

    @Nonnull
    @CanIgnoreReturnValue
    public static String notEmpty(String value)
            throws NullPointerException, IllegalArgumentException {
        return notEmpty(value, "value");
    }

    @Nonnull
    @CanIgnoreReturnValue
    public static String notBlank(String value, @Nonnull String name)
            throws NullPointerException, IllegalArgumentException {
        Checks.notNull(value, name);
        if(value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank!");
        return value;
    }

    @Nonnull
    @CanIgnoreReturnValue
    public static String notBlank(String value)
            throws NullPointerException, IllegalArgumentException {
        return notBlank(value, "value");
    }

    // === NUMBERS ===

    @CanIgnoreReturnValue
    public static int notNegative(int value, @Nonnull String name)
            throws IllegalArgumentException {
        if(value < 0) throw new IllegalArgumentException(name + " cannot be negative!");
        return value;
    }

    @CanIgnoreReturnValue
    public static int notNegative(int value)
            throws IllegalArgumentException {
        return notNegative(value, "value");
    }

    @CanIgnoreReturnValue
    public static long notNegative(long value, @Nonnull String name)
            throws IllegalArgumentException {
        if(value < 0L) throw new IllegalArgumentException(name + " cannot be negative!");
        return value;
    }

    @CanIgnoreReturnValue
    public static long notNegative(long value)
            throws IllegalArgumentException {
        return notNegative(value, "value");
    }

}
