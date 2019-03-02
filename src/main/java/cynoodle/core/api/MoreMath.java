/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

public final class MoreMath {
    private MoreMath() {}

    // The sum of the first m integers: 0 -> 0; 1,2,3,...
    public static long sumFirstIntegers(long m) {
        if(m < 0) throw new IllegalArgumentException("Argument cannot be negative: "+m);
        return (m*(m+1)) / 2;
    }

    // The sum of the first m squares: 0 -> 0; 1,2,3,...
    public static long sumFirstSquares(long m) {
        if(m < 0) throw new IllegalArgumentException("Argument cannot be negative: "+m);
        return (m*(m+1)*((2*m)+1)) / 6;
    }
}
