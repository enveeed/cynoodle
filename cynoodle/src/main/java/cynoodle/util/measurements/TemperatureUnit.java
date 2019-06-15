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
