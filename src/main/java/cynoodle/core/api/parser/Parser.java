/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.parser;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A parser is an object which can transform {@link String Strings}
 * in a specific expected format into an object of type {@link T}.
 * @param <T> the result type
 */
public interface Parser<T> {

    /**
     * Parse the given input into a result object.
     * @param input the input string
     * @return the result object
     * @throws ParsingException if parsing of the input failed, e.g. if the format
     * was not of the expected form or the input does not fit into the range of the expected type.
     */
    @Nonnull
    T parse(@Nonnull String input) throws ParsingException;

    // ===

    /**
     * Return a {@link Function} of this parser which calls {@link #parse(String)}
     * for its input.
     * @return a parser function
     */
    @Nonnull
    default Function<String, T> asFunction() {
        return this::parse;
    }

}
