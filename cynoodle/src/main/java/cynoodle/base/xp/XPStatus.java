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

import com.google.common.flogger.FluentLogger;
import cynoodle.util.Checks;
import cynoodle.discord.MEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.FluentValues;

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
