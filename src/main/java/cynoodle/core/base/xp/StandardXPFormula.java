/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.MoreMath;

public final class StandardXPFormula implements XPFormula {

    @Override
    public int getReachedLevel(long xp) {
        int level = 0;
        while (getRequiredXP(level) <= xp) level ++;
        if(level == 0) return 0;
        else return level - 1;
    }

    @Override
    public long getRequiredXP(int level) {
        return f(level);
    }

    // ===

    // the total required XP for the given level x: 0 -> 0; 1,2,3,...
    // https://math.stackexchange.com/questions/2988646/
    private long f(long x) {

        if(x < 0) throw new IllegalArgumentException("Argument cannot be negative: "+x);
        if(x == 0) return 0; // LVL 0 -> 0 XP

        return (5 * MoreMath.sumFirstSquares(x - 1)) + (50 * MoreMath.sumFirstIntegers(x - 1)) + (100 * x);
    }

}
