/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.measurements;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

/**
 * A unit of mass.
 */
public final class MassUnit extends Unit<MassUnit> {

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

    private MassUnit(String identifier, String symbol, String name,
                     DoubleUnaryOperator toKilogram, DoubleUnaryOperator fromKilogram) {
        super(Quantity.MASS, identifier, symbol, name);
        this.toKilogram = toKilogram;
        this.fromKilogram = fromKilogram;
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
