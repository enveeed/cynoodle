/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import com.google.common.flogger.FluentLogger;
import com.google.common.util.concurrent.AtomicDouble;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MemberKey;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

final class SpamFilterCache {
    SpamFilterCache() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    private final Map<MemberKey, AtomicDouble> status = new HashMap<>();
    private final Map<MemberKey, Instant> times = new HashMap<>();

    // ===

    public final double modify(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user,
                               double delta) {

        MemberKey key = MemberKey.of(guild, user);

        AtomicDouble val = status.computeIfAbsent(key, k -> new AtomicDouble(0d));

        if(times.containsKey(key)) {

            Instant instant = times.get(key);
            long age = Duration.between(instant, Instant.now())
                    .toMinutes();

            double updated = val.addAndGet(-age);
            if(updated < 0) val.set(0);

            LOG.atFinest().log("spam status for %s decreased by %s (minutes)", key, age);
        }

        times.put(key, Instant.now());

        double mod = val.addAndGet(delta);

        LOG.atFiner().log("spam status for %s is now %s", key, mod);

        return mod;
    }

}
