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
