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

package cynoodle.base.spamfilter;

import com.google.common.util.concurrent.AtomicDouble;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.MemberKey;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

final class SpamFilterCache {
    SpamFilterCache() {}

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
        }

        times.put(key, Instant.now());

        double mod = val.addAndGet(delta);

        return mod;
    }

}
