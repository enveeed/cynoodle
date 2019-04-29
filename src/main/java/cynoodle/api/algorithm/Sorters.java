/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api.algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Contains factory methods for common types of {@link Sorter Sorters}.
 */
public final class Sorters {
    private Sorters() {}

    // ===

    /**
     * Create a sorter which sorts elements based on comparing them
     * using the given comparator.
     * @param comparator the comparator
     * @param <T> the type of element
     * @return a comparator based sorter
     */
    @Nonnull
    public static <T> Sorter<T> comparing(@Nonnull Comparator<T> comparator) {
        return new Sorter<>() {
            @Nonnull
            @Override
            public List<T> sort(@Nonnull List<T> input) throws IllegalArgumentException {

                List<T> sorted = Lists.newArrayList(input);

                sorted.sort(comparator);

                return sorted;
            }
        };
    }

    //

    /**
     * <p>Create a sorter for topological sorting of elements in a directed graph.
     * The given function should return elements which have incoming edges to the given element.</p>
     *
     * <p>As an example, this can be used to sort dependencies, in this case the function should return all elements on which the
     * given element depends on.</p>
     *
     * <p>{@link Sorter#sort(List)} may throw {@link IllegalArgumentException} if there are cycles in the arrangement of
     * the elements in the input list which prevents correct topological sorting of the input elements (cycles in the directed graph).</p>
     *
     * @param supplier a function returning a set of elements which have incoming edges to a given element
     * @param <T> the type of element
     * @return a topological sorter
     */
    @Nonnull
    public static <T> Sorter<T> topological(@Nonnull Function<T, Set<T>> supplier) {
        return new Sorter<>() {

            @Nonnull
            @Override
            public List<T> sort(@Nonnull List<T> input) throws IllegalArgumentException {

                List<T> sorted = Lists.newArrayList();
                Set<T> visited = Sets.newHashSet();

                //

                for (T element : input) visit(element, visited, sorted, supplier);

                //

                return Collections.unmodifiableList(sorted);
            }

            private void visit(T element, @Nonnull Set<T> visited, @Nonnull List<T> sorted, @Nonnull Function<T, Set<T>> supplier)
                    throws IllegalArgumentException {

                if (!visited.contains(element)) {

                    visited.add(element);

                    for (T dependency : supplier.apply(element)) {
                        visit(dependency, visited, sorted, supplier);
                    }

                    sorted.add(element);
                } else if (!sorted.contains(element))
                    throw new IllegalArgumentException("There is a circular dependency on element \""+element+"\", " +
                            "preventing correct topological sorting!");
            }
        };
    }

}
