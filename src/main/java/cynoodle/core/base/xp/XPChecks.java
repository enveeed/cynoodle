/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;

/**
 * Validity checks for any values related to the XP system.
 */
public final class XPChecks {
    private XPChecks() {}

    // ===

    public static boolean isValidLevel(int level) {
        return level >= 0;
    }

    public static boolean isValidXP(long xp) {
        return xp >= 0;
    }

    public static boolean isValidRankName(@Nonnull String name) {
        return true; // TODO actually check
    }

    // ===

    @CanIgnoreReturnValue
    public static int validLevel(int level) throws IllegalArgumentException {
        if(!isValidLevel(level)) throw new IllegalArgumentException("Invalid level value: " + level);
        return level;
    }

    @CanIgnoreReturnValue
    public static long validXP(long xp) throws IllegalArgumentException {
        if(!isValidXP(xp)) throw new IllegalArgumentException("Invalid XP value: " + xp);
        return xp;
    }

    @Nonnull
    @CanIgnoreReturnValue
    public static String validRankName(@Nonnull String name) throws IllegalArgumentException {
        if(!isValidRankName(name)) throw new IllegalArgumentException("Invalid Rank name: " + name);
        return name;
    }
}
