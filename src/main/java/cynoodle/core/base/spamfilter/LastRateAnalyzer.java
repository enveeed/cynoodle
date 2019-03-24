/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import cynoodle.core.discord.MemberKey;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes the time between the last and the current message.
 *
 * 0% = last message 10+ seconds ago
 * 100% = last message 100 ms ago or less
 */
public final class LastRateAnalyzer implements SpamAnalyzer {
    LastRateAnalyzer() {}

    private final Map<MemberKey, Long> tracker = new HashMap<>();

    @Override
    public double analyze(@Nonnull GuildMessageReceivedEvent event) {

        MemberKey key = MemberKey.of(event.getMember());

        long time = event.getMessage().getCreationTime()
                .toInstant().toEpochMilli();
        long last = this.tracker.getOrDefault(key, -1L);

        double score;

        if(last == -1L) score = 0d;
        else score = (1d / (time - last)) * 100;

        this.tracker.put(key, time);

        return score;
    }
}
