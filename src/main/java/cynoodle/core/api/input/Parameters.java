/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.input;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;

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

    public boolean has(int index) {
        return index >= 0 && index < this.parameters.length;
    }

    // ===

    @Nonnull
    public Optional<String> get(int index) {
        if(has(index)) return Optional.of(this.parameters[index]);
        else return Optional.empty();
    }

    // ===

    @Nonnull
    public <T> Optional<T> getAs(int index, @Nonnull Parser<T> parser) throws ParserException {
        // re-wrap the optional because functional map() cannot handle checked exceptions.
        Optional<String> opt = get(index);
        if(opt.isPresent()) return Optional.of(parser.parse(opt.orElseThrow()));
        else return Optional.empty();
    }

    // ===

    @Nonnull
    public String join() {
        return Joiner.on(' ').join(this.parameters);
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
