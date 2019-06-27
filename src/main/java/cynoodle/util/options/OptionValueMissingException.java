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

import javax.annotation.Nonnull;

/**
 * Thrown by {@link Options#parse(String)} when there was an option in the input string
 * which required a value, but there was no value.
 */
public final class OptionValueMissingException extends OptionsException {

    private final Option option;

    // ===

    OptionValueMissingException(@Nonnull String message, @Nonnull Option option) {
        super(message);
        this.option = option;
    }

    // ===

    /**
     * Get the option which required a value which was missing and caused this exception.
     * @return the cause option
     */
    @Nonnull
    public Option getOption() {
        return this.option;
    }
}
