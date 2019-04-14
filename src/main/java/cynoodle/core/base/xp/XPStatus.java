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
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The XP status of a Member.
 */
@EIdentifier("base:xp:status")
public final class XPStatus extends MEntity {
    private XPStatus() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    /**
     * The current XP value.
     */
    private final AtomicLong value = new AtomicLong(0L);

    /**
     * The timeout (-until) timestamp.
     */
    private Instant timeout = Instant.now().minusMillis(100);

    // === VALUE ===

    public long addXP(long value) {
        Checks.notNegative(value, "value");
        return this.value.getAndUpdate(x -> {
            long next = x + value;
            LOG.atFinest().log("Adding XP for %s: %s + %s -> %s", requireUser(), x, value, next);
            return next;
        });
    }

    public long removeXP(long value) {
        Checks.notNegative(value, "value");
        return this.value.getAndUpdate(x -> {
            long next = x - value;
            if (next < 0L) next = 0L;
            LOG.atFinest().log("Removing XP for %s: %s + %s -> %s", requireUser(), x, value, next);
            return next;
        });
    }

    //

    public long getXP() {
        return this.value.get();
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

    @Nonnull
    public static Comparator<XPStatus> orderAscending() {
        return Comparator.comparingLong(XPStatus::getXP);
    }

    @Nonnull
    public static Comparator<XPStatus> orderDescending() {
        return Comparator.comparingLong(XPStatus::getXP).reversed();
    }
}
