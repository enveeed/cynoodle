/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.graph;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public interface IGraph {

    /**
     * Query the given field.
     * @param field the field
     * @return the value of the field
     * @throws NoSuchElementException if there is no field of this name on this object
     */
    Object query(@Nonnull String field) throws NoSuchElementException;

}
