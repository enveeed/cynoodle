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

package cynoodle.util.options;

import cynoodle.util.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines an available option for parsing using {@link Options}.
 *
 * <p>
 *     Options require a <b>long name</b> for input in the form of <code>--option</code>
 *     and can optionally have a <b>short name</b> to allow input in the form of <code>-o</code>.
 * </p>
 * <p>
 *     Options can also either act as <b>flags</b> ({@link #newFlagOption(String, Character)}) or
 *     as <b>value options</b> ({@link #newValueOption(String, Character)}),
 *     where the latter requires a value to be given.
 * </p>
 */
public final class Option {

    private final String option_long;
    private final Character option_short;

    private final boolean requires_value;

    // ===

    private Option(@Nonnull String option_long, @Nullable Character option_short, boolean requires_value) {
        this.option_long = option_long;
        this.option_short = option_short;
        this.requires_value = requires_value;
    }

    // ===

    /**
     * Get the long form of this option.
     *
     * @return the option long form
     */
    @Nonnull
    public String getLong() {
        return this.option_long;
    }

    /**
     * Get the short form of this option.
     *
     * @return the option short form
     */
    @Nullable
    public Character getShort() {
        return this.option_short;
    }

    //

    /**
     * Get if this option requires a value when used.
     *
     * @return true if it requires a value, false if not
     */
    public boolean isValueRequired() {
        return this.requires_value;
    }

    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        return option_long.equals(option.option_long);
    }

    @Override
    public int hashCode() {
        return option_long.hashCode();
    }

    // ===

    @Nonnull
    public static Option newValueOption(@Nonnull String option_long, @Nullable Character option_short) {
        Checks.notEmpty(option_long);
        return new Option(option_long, option_short, true);
    }

    @Nonnull
    public static Option newFlagOption(@Nonnull String option_long, @Nullable Character option_short) {
        Checks.notEmpty(option_long);
        return new Option(option_long, option_short, false);
    }

}
