/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import cynoodle.core.discord.MemberKey;
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
