/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api.measurements;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

/**
 * A unit of temperature.
 */
public final class TemperatureUnit extends BaseUnit<TemperatureUnit> {

    public static final TemperatureUnit KELVIN =
            new TemperatureUnit("kelvin", "K", "Kelvin",
                    x -> x, x -> x);

    public static final TemperatureUnit CELSIUS =
            new TemperatureUnit("celsius", "°C", "Degrees Celsius",
                    x -> x + 273.15d, x -> x - 273.15d);
    public static final TemperatureUnit FAHRENHEIT =
            new TemperatureUnit("fahrenheit", "°F", "Degrees Fahrenheit",
            x -> (x + 459.67d) * 5d/9d, x -> (x * 1.8d) - 459.67d);

    // ===

    private final DoubleUnaryOperator toKelvin;
    private final DoubleUnaryOperator fromKelvin;

    // ===

    private TemperatureUnit(String identifier, String symbol, String name,
                            DoubleUnaryOperator toKelvin, DoubleUnaryOperator fromKelvin) {
        super(identifier, symbol, name);
        this.toKelvin = toKelvin;
        this.fromKelvin = fromKelvin;
    }

    // ===

    @Override
    public Quantity quantity() {
        return Quantity.TEMPERATURE;
    }

    // ===

    @Override
    public DoubleUnaryOperator to(@Nonnull TemperatureUnit other) {
        return operand -> this.fromKelvin.applyAsDouble(other.toKelvin.applyAsDouble(operand));
    }

    // ===

    @Nonnull
    public static Optional<TemperatureUnit> of(@Nonnull String identifier) {
        switch (identifier) {
            case    "kelvin":       return Optional.of(KELVIN);
            case    "celsius":      return Optional.of(CELSIUS);
            case    "fahrenheit":   return Optional.of(FAHRENHEIT);
            default: return Optional.empty();
        }
    }
}
