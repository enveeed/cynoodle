/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api.algorithm;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A sorter for sorting elements of list.
 * @param <T> the type of element
 * @see #sort(List)
 * @see Sorters
 */
public interface Sorter<T> {

    /**
     * Sort the elements in the input list and return them in a sorted output list without
     * changing the input list.
     * @param input the input list
     * @return the sorted output list
     * @throws IllegalArgumentException if the input list is not sortable (depends on the implementation)
     */
    @Nonnull
    List<T> sort(@Nonnull List<T> input) throws IllegalArgumentException;

    // ===

    /**
     * Replace the contents of the given list by the output of {@link #sort(List)} using the list as the input.
     * @param list the list
     * @throws IllegalArgumentException if the input list is not sortable (depends on the implementation)
     * @see #sort(List)
     */
    default void sortReplace(@Nonnull List<T> list) throws IllegalArgumentException {

        List<T> sorted = sort(list);

        //

        list.clear();
        list.addAll(sorted);
    }

}
