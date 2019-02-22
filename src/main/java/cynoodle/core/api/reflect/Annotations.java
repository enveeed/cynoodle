/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.reflect;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Annotations {
    private Annotations() {}

    // ===

    @Nonnull
    public static <A extends Annotation> Set<A> collect(@Nonnull Class<?> cl, @Nonnull Class<A> annotation, @Nonnull Class<?> limit) {

        Set<A> annotations = new HashSet<>();

        Class<?> current = cl;

        while (current != null) {

            annotations.addAll(Arrays.asList(current.getDeclaredAnnotationsByType(annotation)));

            if(current == limit) break;

            current = current.getSuperclass();
        }

        return Collections.unmodifiableSet(annotations);
    }

    @Nonnull
    public static <A extends Annotation> Set<A> collectOf(@Nonnull Class<?> cl, @Nonnull Class<A> annotation) {
        return collect(cl, annotation, cl);
    }

    @Nonnull
    public static <A extends Annotation> Set<A> collectAll(@Nonnull Class<?> cl, @Nonnull Class<A> annotation) {
        return collect(cl, annotation, Object.class);
    }
}
