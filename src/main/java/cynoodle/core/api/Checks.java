/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;

/**
 * validity checks for single values.
 */
public final class Checks {
    private Checks() {}

    // === NULL ===

    @Nonnull
    public static <T> T notNull(T value, @Nonnull String name)
            throws NullPointerException {
        if(value == null) throw new NullPointerException(name + " cannot be null!");
        return value;
    }

    @Nonnull
    public static <T> T notNull(T value)
            throws NullPointerException {
        return notNull(value, "value");
    }

    // === STRINGS ===

    @Nonnull
    public static String notEmpty(String value, @Nonnull String name)
            throws NullPointerException, IllegalArgumentException {
        Checks.notNull(value, name);
        if(value.isEmpty()) throw new IllegalArgumentException(name + " cannot be empty!");
        return value;
    }

    @Nonnull
    public static String notEmpty(String value)
            throws NullPointerException, IllegalArgumentException {
        return notEmpty(value, "value");
    }

    @Nonnull
    public static String notBlank(String value, @Nonnull String name)
            throws NullPointerException, IllegalArgumentException {
        Checks.notNull(value, name);
        if(value.isBlank()) throw new IllegalArgumentException(name + " cannot be blank!");
        return value;
    }

    @Nonnull
    public static String notBlank(String value)
            throws NullPointerException, IllegalArgumentException {
        return notBlank(value, "value");
    }

    // === NUMBERS ===

    public static int notNegative(int value, @Nonnull String name)
            throws IllegalArgumentException {
        if(value < 0) throw new IllegalArgumentException(name + " cannot be negative!");
        return value;
    }

    public static int notNegative(int value)
            throws IllegalArgumentException {
        return notNegative(value, "value");
    }

    public static long notNegative(long value, @Nonnull String name)
            throws IllegalArgumentException {
        if(value < 0L) throw new IllegalArgumentException(name + " cannot be negative!");
        return value;
    }

    public static long notNegative(long value)
            throws IllegalArgumentException {
        return notNegative(value, "value");
    }

}
