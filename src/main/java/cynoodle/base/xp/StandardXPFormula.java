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

package cynoodle.base.xp;

import cynoodle.api.MoreMath;

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
