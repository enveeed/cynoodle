/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.Checks;
import cynoodle.core.discord.MEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import cynoodle.core.mongo.fluent.FluentValues;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The XP status of a Member.
 */
@EIdentifier("base:xp:status")
public final class XPStatus extends MEntity implements Comparable<XPStatus> {
    private XPStatus() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    /**
     * The current XP.
     */
    private final AtomicLong value = new AtomicLong(0L);

    /**
     * The timeout (-until) timestamp.
     */
    private Instant timeout = Instant.now()
            .minusMillis(100);

    // === VALUE ===

    public long add(long value) {
        Checks.notNegative(value, "value");
        return this.value.getAndUpdate(x -> x + value);
    }

    public long remove(long value) {
        Checks.notNegative(value, "value");
        return this.value.getAndUpdate(x -> {
            long next = x - value;
            if (next < 0L) next = 0L;
            return next;
        });
    }

    // TODO temporary for legacy data
    public long set(long value) {
        Checks.notNegative(value, "value");
        return this.value.getAndUpdate(x -> value);
    }

    //

    public long get() {
        return this.value.get();
    }

    //

    void addAndPersist(long xp) {
        add(xp);
        persist();
    }

    void removeAndPersist(long xp) {
        remove(xp);
        persist();
    }

    // === TIMEOUT ===

    @Nonnull
    public Instant getTimeout() {
        return this.timeout;
    }

    public void setTimeout(@Nonnull Instant timeout) {
        this.timeout = timeout;
    }

    //

    public boolean isInTimeout() {
        return getTimeout().isAfter(Instant.now());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.value.set(source.getAt("value").asLong().or(this.value.get()));
        this.timeout = source.getAt("timeout").as(FluentValues.toInstant()).or(this.timeout);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("value").asLong().to(this.value.get());
        data.setAt("timeout").as(FluentValues.fromInstant()).to(this.timeout);

        return data;
    }

    // ===

    @Override
    public int compareTo(@Nonnull XPStatus o) {

        // this is the reverse order, that means more XP comes first
        // (natural order for XP)

        return Long.compare(o.get(), this.get());
    }
}
