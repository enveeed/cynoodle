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

package cynoodle.util.measurements;

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
