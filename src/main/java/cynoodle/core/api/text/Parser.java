/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface Parser<T> extends Function<String, T> {

    T parse(@Nonnull String input) throws ParserException;

    @Override
    default T apply(String s) {
        try {
            return parse(s);
        } catch (ParserException e) {
            throw new RuntimeException("Failed to parse input: " + s, e);
        }
    }
}
