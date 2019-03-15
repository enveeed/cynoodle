/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface Parser<T> {

    @Nonnull
    T parse(@Nonnull String input) throws ParsingException;

    // ===

    @Nonnull
    default Function<String, T> asFunction() {
        return this::parse;
    }

}
