/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api.reflect;

import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Primitives;

import javax.annotation.Nonnull;
import java.lang.reflect.Executable;
import java.util.Arrays;

public final class ParameterTypes {

    private final Class<?>[] types;

    //

    private ParameterTypes(@Nonnull Class<?>[] types) {
        this.types = types;
    }

    //

    @Nonnull
    public Class<?>[] toArray() {
        return types;
    }

    //

    public int size() {
        return this.types.length;
    }

    //

    /**
     * Checks if the specified parameters are applicable to these parameter types.
     * If this method returns true then the parameters should be applicable to methods
     * with these exact parameter types.
     * <p/>
     * A parameter matches if its either directly assignable, assignable after being unwrapped to a primitive type
     * or if its null and assigned to a non-primitive type.
     *
     * @param parameters the parameters to check
     * @return true if applicable, false if not applicable
     */
    public boolean isApplicable(@Nonnull Object... parameters) {
        if(this.types.length != parameters.length) return false;

        // test every type <-> parameter combination
        for (int i = 0; i < this.types.length; i++) {

            Class<?> type = this.types[i];
            Object parameter = parameters[i];

            if(parameter == null) {
                // null can not be auto-unwrapped to a primitive
                if(type.isPrimitive()) return false;
            }
            else {
                if(type.isPrimitive()) {
                    // check if parameter can be auto-unwrapped to a primitive type and if it matches the type
                    if(type != Primitives.unwrap(parameter.getClass())) return false;
                }
                else {
                    // check simple assignment
                    if(!type.isAssignableFrom(parameter.getClass())) return false;
                }
            }
        }

        return true; // all checks passed
    }

    //

    @Nonnull
    public static ParameterTypes of(@Nonnull Class<?>... types) {
        return new ParameterTypes(Arrays.copyOf(types, types.length));
    }

    @Nonnull
    public static ParameterTypes of(@Nonnull Iterable<Class<?>> types) {
        return of(Iterables.toArray(types, Class.class));
    }

    //

    @Nonnull
    public static ParameterTypes of(@Nonnull Executable executable) {
        return of(executable.getParameterTypes());
    }

    //

    @Nonnull
    public static ParameterTypes concat(@Nonnull ParameterTypes... types) {
        Class<?>[] combined = new Class<?>[0];
        for (ParameterTypes type : types)
            combined = ObjectArrays.concat(combined, type.types, Class.class);
        return of(combined);
    }

    @Nonnull
    public static ParameterTypes concat(@Nonnull Iterable<ParameterTypes> types) {
        return concat(Iterables.toArray(types, ParameterTypes.class));
    }
}

