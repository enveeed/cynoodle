/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * A unit for temperature.
 */
public final class TemperatureUnit {

    public static final TemperatureUnit KELVIN = new TemperatureUnit("K", "Kelvin",
            x -> x, x -> x);

    public static final TemperatureUnit CELSIUS = new TemperatureUnit("°C", "Degrees Celsius",
            x -> x + 273.15d, x -> x - 273.15d);
    public static final TemperatureUnit FAHRENHEIT = new TemperatureUnit("°F", "Degrees Fahrenheit",
            x -> (x + 459.67d) * 5d/9d, x -> (x * 1.8d) - 459.67d);

    // ===

    private final String symbol;
    private final String displayName;

    private final DoubleUnaryOperator toKelvin;
    private final DoubleUnaryOperator fromKelvin;

    // ===

    private TemperatureUnit(@Nonnull String symbol, @Nonnull String displayName,
                           @Nonnull DoubleUnaryOperator toKelvin, @Nonnull DoubleUnaryOperator fromKelvin) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.toKelvin = toKelvin;
        this.fromKelvin = fromKelvin;
    }

    // ===

    @Nonnull
    public String getSymbol() {
        return this.symbol;
    }

    //

    @Nonnull
    public String getDisplayName() {
        return this.displayName;
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemperatureUnit that = (TemperatureUnit) o;

        return Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    // ===

    public double to(@Nonnull TemperatureUnit other, double value) {
        return other.fromKelvin.applyAsDouble(this.toKelvin.applyAsDouble(value));
    }

    // ===

    @Nonnull
    public static TemperatureUnit get(@Nonnull String unit) throws IllegalArgumentException {
        if(unit.equalsIgnoreCase("kelvin")) return KELVIN;
        if(unit.equalsIgnoreCase("celsius")) return CELSIUS;
        if(unit.equalsIgnoreCase("fahrenheit")) return FAHRENHEIT;
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
}