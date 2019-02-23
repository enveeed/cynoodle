/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility for pseudo-random numbers and other pseudo-random actions
 */
public class Random {

    private Random() {}

    /**
     * Get a pseudo-random int value between min and max (inclusive)
     * @param min minimum value
     * @param max maximum value
     * @return random value
     */
    public static int nextInt(int min, int max) {
        if(min==max) return min;
        if(max<min) {
            int v = max;
            max = min;
            min = v;
        }
        return ThreadLocalRandom.current().nextInt(min,max+1);
    }

    /**
     * Get a pseudo-random long value between min and max (inclusive)
     * @param min minimum value
     * @param max maximum value
     * @return random value
     */
    public static long nextLong(long min, long max) {
        if(min==max) return min;
        if(max<min) {
            long v = max;
            max = min;
            min = v;
        }
        return ThreadLocalRandom.current().nextLong(min,max+1);
    }

    /**
     * Get a pseudo-random boolean value
     * @return random value
     */
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

}
