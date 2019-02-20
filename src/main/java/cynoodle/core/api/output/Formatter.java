/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.output;

import javax.annotation.Nonnull;

/**
 * A formatter to format an input object as String output in a specific way.
 * @param <T> the input type
 */
public interface Formatter<T> {

    @Nonnull
    String format(@Nonnull T input);

}
