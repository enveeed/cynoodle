/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.measurements;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

/**
 * A unit of a specific quantity. (internal)
 * @param <U> the unit type itself.
 */
abstract class Unit<U extends Unit<U>> {

    private final Quantity quantity;
    private final String identifier;

    private final String symbol;
    private final String name;

    //

    Unit(Quantity quantity, String identifier, String symbol, String name) {
        this.quantity = quantity;
        this.identifier = identifier;
        this.symbol = symbol;
        this.name = name;
    }

    //

    /**
     * Get the quantity of this Unit.
     * @return the unit quantity
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Get the unique identifier of this Unit.
     * @return the unit identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    //

    /**
     * Get the symbol of this Unit.
     * @return the unit symbol
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Get the name of this Unit.
     * @return the unit name
     */
    public String getName() {
        return this.name;
    }

    // ===

    /**
     * Create a function which converts values in this Unit to the given other Unit.
     * @param other the other unit
     * @return a converter function.
     */
    public abstract DoubleUnaryOperator to(@Nonnull U other);
}
