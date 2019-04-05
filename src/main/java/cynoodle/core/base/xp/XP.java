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
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The XP of a Member.
 */
@EIdentifier("base:xp:xp")
public final class XP extends MEntity implements Comparable<XP> {
    private XP() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final XPModule module = Module.get(XPModule.class);

    // ===

    private final AtomicLong xp = new AtomicLong(0L);

    // ===

    long add(long value) {
        Checks.notNegative(value, "value");

        return this.xp.getAndUpdate(x -> x + value);
    }

    long remove(long value) {
        Checks.notNegative(value, "value");

        return this.xp.getAndUpdate(x -> {
            long next = x - value;
            if (next < 0L) next = 0L;
            return next;
        });
    }

    // TODO temporary for legacy data
    public long set(long value) {
        Checks.notNegative(value, "value");

        return this.xp.getAndUpdate(x -> value);
    }

    //

    public long get() {
        return this.xp.get();
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

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.xp.set(source.getAt("xp").asLong().or(this.xp.get()));
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("xp").asLong().to(this.xp.get());

        return data;
    }

    // ===

    @Override
    public int compareTo(@Nonnull XP o) {

        // this is the reverse order, that means more XP comes first
        // (natural order for XP)

        return Long.compare(o.get(), this.get());
    }
}
