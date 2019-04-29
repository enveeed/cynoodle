/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp;

public interface XPFormula {

    int getReachedLevel(long xp);

    long getRequiredXP(int level);

}
