/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.input;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
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

    @Nonnull
    public String get(int index) throws ArrayIndexOutOfBoundsException {
        return this.parameters[index];
    }

    public boolean has(int index) {
        return index >= 0 && index < this.parameters.length;
    }

    // ===

    @Nonnull
    public static Parameters of(@Nonnull String[] parameters) {
        return new Parameters(parameters);
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
