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

package cynoodle.util.algorithm;

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
