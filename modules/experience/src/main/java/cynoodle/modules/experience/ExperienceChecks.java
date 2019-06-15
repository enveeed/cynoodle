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

package cynoodle.modules.experience;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public final class ExperienceChecks {
    private ExperienceChecks() {}

    // ===

    /**
     * This regex matches anything that does not include new-lines and common markdown formatting characters.
     */
    private static final Pattern REGEX_NAMES = Pattern.compile("^[^\\r\\n\\*\\_\\~]+$");

    // ===

    public static boolean isValidName(@Nonnull String name) {
        return REGEX_NAMES.matcher(name).matches();
    }

    public static boolean isValidLevel(int level) {
        return level >= Experience.MIN_LEVEL && level <= Experience.MAX_LEVEL;
    }

    public static boolean isValidValue(long value) {
        return value >= Experience.MIN_VALUE && value <= Experience.MAX_VALUE;
    }

    // ===

    @Nonnull
    @CanIgnoreReturnValue
    public static String validName(@Nonnull String name) throws IllegalArgumentException {
        if(!isValidName(name)) throw new IllegalArgumentException("Invalid name: " + name);
        return name;
    }

    @CanIgnoreReturnValue
    public static int validLevel(int level) throws IllegalArgumentException {
        if(!isValidLevel(level)) throw new IllegalArgumentException("Invalid level: " + level);
        return level;
    }

    @CanIgnoreReturnValue
    public static long validValue(long value) throws IllegalArgumentException {
        if(!isValidValue(value)) throw new IllegalArgumentException("Invalid value: " + value);
        return value;
    }

}
