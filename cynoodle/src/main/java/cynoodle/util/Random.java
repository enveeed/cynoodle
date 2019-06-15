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

package cynoodle.util;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility for pseudo-random numbers and other pseudo-random actions
 */
public final class Random {
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

    /**
     * Get a pseudo-randomly selected value from the given collection.
     * @param collection the collection
     * @param <T> the collections type
     * @return randomly selected value
     */
    public static <T> T nextOf(Collection<T> collection) {
        if(collection.size() == 0) throw new IllegalArgumentException();
        int index = nextInt(0, collection.size() - 1);
        for(T entry : collection) if (--index < 0) return entry;
        throw new AssertionError();
    }

    /**
     * Get a pseudo-randomly selected value from the given array.
     * @param array the array
     * @param <T> the collections type
     * @return randomly selected value
     */
    public static <T> T nextOf(T[] array) {
        if(array.length == 0) throw new IllegalArgumentException();
        int index = nextInt(0, array.length - 1);
        for(T entry : array) if (--index < 0) return entry;
        throw new AssertionError();
    }

}
