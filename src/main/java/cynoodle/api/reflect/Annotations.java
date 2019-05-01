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

package cynoodle.api.reflect;

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
