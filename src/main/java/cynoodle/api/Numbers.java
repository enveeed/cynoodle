/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Numbers {
    private Numbers() {}

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = createFormatSymbols();

    // === FORMATTING ===

    @Nonnull
    public static String format(long num) {
        return formatterInteger().format(num);
    }

    @Nonnull
    public static String format(double num) {
        return formatterDecimal(2).format(num);
    }

    @Nonnull
    public static String format(double num, int maxFractionDigits) {
        return formatterDecimal(maxFractionDigits).format(num);
    }

    //

    @Nonnull
    private static DecimalFormat formatter(@Nonnull String pattern) {
        DecimalFormat format = new DecimalFormat(pattern, DECIMAL_FORMAT_SYMBOLS);
        format.setGroupingSize(3);
        format.setDecimalSeparatorAlwaysShown(false);
        return format;
    }

    //

    @Nonnull
    private static DecimalFormat formatterInteger() {
        DecimalFormat format = formatter("###,###,###,###");
        format.setMaximumFractionDigits(0);
        return format;
    }

    @Nonnull
    private static DecimalFormat formatterDecimal(int maxFractionDigits) {
        DecimalFormat format = formatter("###,###,###,##0.000000000");
        format.setMaximumFractionDigits(maxFractionDigits);
        return format;
    }

    //

    @Nonnull
    private static DecimalFormatSymbols createFormatSymbols() {

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH);

        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');

        symbols.setExponentSeparator("×10^");

        symbols.setPercent('%');
        symbols.setMinusSign('-');
        symbols.setNaN("NaN");
        symbols.setInfinity("Infinity");

        return symbols;
    }
}
