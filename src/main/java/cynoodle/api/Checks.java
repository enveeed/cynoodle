/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
