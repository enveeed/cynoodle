/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.measurements;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

/**
 * A unit of a specific quantity.
 * @param <U> the unit type itself.
 */
interface Unit<U extends Unit<U>> {

    /**
     * Get the quantity of this Unit.
     * @return the unit quantity
     */
    Quantity quantity();

    /**
     * Get the unique identifier of this Unit.
     * @return the unit identifier
     */
    String identifier();

    //

    /**
     * Get the symbol of this Unit.
     * @return the unit symbol
     */
    String symbol();

    /**
     * Get the name of this Unit.
     * @return the unit name
     */
    String name();

    // ===

    /**
     * Format a value of this unit.
     * @param value the value
     * @return a formatted string for a value of this unit.
     */
    @Nonnull
    default String format(double value) {
        return value + " " + this.symbol();
    }

    // ===

    /**
     * Create a function which converts values in this Unit to the given other Unit.
     * @param other the other unit
     * @return a converter function.
     */
    DoubleUnaryOperator to(@Nonnull U other);
}
