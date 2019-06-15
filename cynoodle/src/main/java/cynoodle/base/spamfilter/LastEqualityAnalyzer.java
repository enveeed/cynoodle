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

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import cynoodle.discord.MemberKey;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes equality of the last and current message using hashes.
 *
 * 0% = not equal
 * 100% = equal
 */
@SuppressWarnings("UnstableApiUsage")
public final class LastEqualityAnalyzer implements SpamAnalyzer {
    LastEqualityAnalyzer() {}

    private static final HashFunction hash = Hashing.sipHash24();

    private final Map<MemberKey, Long> tracker = new HashMap<>();

    @Override
    public double analyze(@Nonnull GuildMessageReceivedEvent event) {

        MemberKey key = MemberKey.of(event.getMember());

        byte[] bytes = event.getMessage()
                .getContentRaw()
                .getBytes(StandardCharsets.UTF_8);

        long hash = LastEqualityAnalyzer.hash.hashBytes(bytes).asLong();
        long last = this.tracker.getOrDefault(key, -1L);

        double score;

        if(last == -1L) score = 0d;
        else if(hash == last) score = 1d;
        else score = 0d;

        this.tracker.put(key, hash);

        return score;

    }
}
