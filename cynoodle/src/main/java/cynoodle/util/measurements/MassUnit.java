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
 * A unit of mass.
 */
public final class MassUnit extends BaseUnit<MassUnit> {

    public static final MassUnit KILOGRAM =
            new MassUnit("kilogram", "kg", "Kilogram",
                    x -> x, x -> x);

    public static final MassUnit GRAM =
            new MassUnit("gram", "g", "Gram",
                    x -> x * 0.001d, x -> x * 1000d);
    public static final MassUnit METRIC_TON =
            new MassUnit("ton", "t", "Ton",
                    x -> x * 1000d, x -> x * 0.001d);

    public static final MassUnit OUNCE =
            new MassUnit("ounce", "oz", "Ounce",
                    x -> x * 0.0283495d, x -> x * 35.274d);
    public static final MassUnit POUND =
            new MassUnit("pound", "lb", "Pound",
                    x -> x * 0.453592d, x -> x * 2.20462d);
    public static final MassUnit STONE =
            new MassUnit("stone","st", "Stone",
                    x -> x * 6.35029d, x -> x * 0.157473d);

    public static final MassUnit LONG_TON =
            new MassUnit("ton_long", "tn. l.", "Long ton",
                    x -> x * 1016.05d, x -> x * 0.000984207d);
    public static final MassUnit SHORT_TON =
            new MassUnit("ton_short", "tn. sh.", "Short ton",
                    x -> x * 907.185d, x -> x * 0.00110231d);

    // ===

    private final DoubleUnaryOperator toKilogram;
    private final DoubleUnaryOperator fromKilogram;

    // ===

    public MassUnit(String identifier, String symbol, String name,
                    DoubleUnaryOperator toKilogram, DoubleUnaryOperator fromKilogram) {
        super(identifier, symbol, name);
        this.toKilogram = toKilogram;
        this.fromKilogram = fromKilogram;
    }

    // ===

    @Override
    public Quantity quantity() {
        return Quantity.MASS;
    }

    // ===

    @Override
    public DoubleUnaryOperator to(@Nonnull MassUnit other) {
        return operand -> this.fromKilogram.applyAsDouble(other.toKilogram.applyAsDouble(operand));
    }

    //

    @Nonnull
    public static Optional<MassUnit> of(@Nonnull String identifier) {
        switch (identifier) {
            case    "kilogram":     return Optional.of(KILOGRAM);
            case    "gram":         return Optional.of(GRAM);
            case    "ton":          return Optional.of(METRIC_TON);
            case    "ounce":        return Optional.of(OUNCE);
            case    "pound":        return Optional.of(POUND);
            case    "stone":        return Optional.of(STONE);
            case    "ton_long":     return Optional.of(SHORT_TON);
            case    "ton_short":    return Optional.of(LONG_TON);
            default: return Optional.empty();
        }
    }
}
