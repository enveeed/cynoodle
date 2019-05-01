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
import java.time.Instant;

/**
 * A snowflake algorithm for unique IDs <br>
 * |    timestamp    | server id | sequence |
 */
public final class Snowflake {

    // default

    private static final long DEFAULT_ID_BITS = 10L;
    private static final long DEFAULT_SEQUENCE_BITS = 12L;

    //

    private final long epoch;
    private final long id;
    private final long idShift;
    private final long timestampLeftShift;
    private final long sequenceMask;

    private volatile long sequence = 0L;
    private volatile long lastTimestamp = -1L;

    private Snowflake(long id, long idBits, long sequenceBits, long epoch) {
        this.id = id;
        this.epoch = epoch;

        this.idShift = sequenceBits;
        this.timestampLeftShift = sequenceBits + idBits;
        this.sequenceMask = ~(-1 << sequenceBits);
    }

    public synchronized long next() {

        long timestamp = currentTime();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Can not generate snowflake because the clock moved backwards.");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0L) {
                timestamp = untilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift) | id << idShift | sequence;
    }

    /**
     * Wait until the given timestamp passed the current time
     * @param lastTimestamp the timestamp to wait for
     * @return the timestamp which first passed the current time
     */
    private long untilNextMillis(long lastTimestamp) {

        long timestamp = currentTime();

        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }

        return timestamp;
    }

    /**
     * Get the current time in ms from EPOCH
     * @return time in ms
     */
    private long currentTime() {
        return Instant.now().toEpochMilli();
    }

    //

    /**
     * Get the creation time of the given snowflake
     * @param snowflake the snowflake
     * @return the creation time
     */
    @Nonnull
    public Instant getCreationTime(long snowflake) {
        long timestamp = (snowflake >>> timestampLeftShift) + epoch;
        return Instant.ofEpochMilli(timestamp);
    }

    //

    /**
     * Create a new Snowflake tool using the provided settings
     * @param id the ID for this snowflake tool
     * @param idBits the amount of bits which shall be used for the ID
     * @param sequenceBits the amount of bits which shall be used for the sequence
     * @param epoch the epoch time in ms from EPOCH to use as the local epoch
     * @return a new snowflake tool
     */
    @Nonnull
    public static Snowflake get(long id, long idBits, long sequenceBits, long epoch) {
        return new Snowflake(id,idBits,sequenceBits,epoch);
    }

    @Nonnull
    public static Snowflake get(long id, long epoch) {
        return get(id,DEFAULT_ID_BITS,DEFAULT_SEQUENCE_BITS,epoch);
    }

    @Nonnull
    public static Snowflake get(long id, @Nonnull Instant epoch) {
        return get(id,epoch.toEpochMilli());
    }

    @Nonnull
    public static Snowflake get(@Nonnull Instant epoch) {
        return get(0L,epoch.toEpochMilli());
    }
}
