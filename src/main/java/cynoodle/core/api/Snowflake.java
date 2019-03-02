/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

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
