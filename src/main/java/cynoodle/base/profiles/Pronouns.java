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

package cynoodle.base.profiles;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum Pronouns {

    /**
     * Addresses the user grammatically masculine.
     */
    MASCULINE("masculine"),
    /**
     * Addresses the user grammatically feminine.
     */
    FEMININE("feminine"),
    /**
     * Addresses the user grammatically indefinite.
     */
    INDEFINITE("indefinite"),

    ;

    private final String key;

    Pronouns(@Nonnull String key) {
        this.key = key;
    }

    // ===

    @Nonnull
    public String key() {
        return this.key;
    }

    // ===

    @Nonnull
    public static Optional<Pronouns> find(@Nonnull String key) {
        if(key.equals(MASCULINE.key()))
            return Optional.of(MASCULINE);
        if(key.equals(FEMININE.key()))
            return Optional.of(FEMININE);
        if(key.equals(INDEFINITE.key()))
            return Optional.of(INDEFINITE);
        return Optional.empty();
    }

}
