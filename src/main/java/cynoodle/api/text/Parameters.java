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

package cynoodle.api.text;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Parameters {

    private String[] parameters;

    // ===

    private Parameters(@Nonnull String[] parameters) {
        this.parameters = parameters;
    }

    // ===

    public int size() {
        return parameters.length;
    }

    // ===

    @Nonnull
    public Stream<String> stream() {
        return Arrays.stream(parameters);
    }

    @Nonnull
    public List<String> list() {
        return stream().collect(Collectors.toList());
    }

    // ===

    /**
     * Check if these parameters contain a parameter at the given index.
     * @param index the index, starting with zero
     * @return true if there is a parameter, false if otherwise
     */
    public boolean has(int index) {
        return index >= 0 && index < this.parameters.length;
    }

    /**
     * Get a parameter at the given index.
     * @param index the index
     * @return an optional containing the parameter if present, otherwise empty
     */
    @Nonnull
    public Optional<String> get(int index) {
        if(has(index)) return Optional.of(this.parameters[index]);
        else return Optional.empty();
    }

    // ===

    @Nonnull
    public String join(char separator) {
        return Joiner.on(separator).join(this.parameters);
    }

    @Nonnull
    public String join() {
        return join(' ');
    }

    // ===

    @Override
    public String toString() {
        return Arrays.toString(this.parameters);
    }

    // ===

    @Nonnull
    public static Parameters of(@Nonnull String[] parameters) {
        return new Parameters(parameters);
    }

    @Nonnull
    public static Parameters of(@Nonnull Collection<String> parameters) {
        return new Parameters(Iterables.toArray(parameters, String.class));
    }

    @Nonnull
    public static Parameters of(@Nonnull String parameters, char separator) {
        return new Parameters(Iterables.toArray(Splitter.on(separator).split(parameters), String.class));
    }

    @Nonnull
    public static Parameters of(@Nonnull String parameters) {
        return of(parameters, ' ');
    }
}
