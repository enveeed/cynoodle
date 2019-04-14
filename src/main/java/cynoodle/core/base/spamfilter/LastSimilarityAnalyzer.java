/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import cynoodle.core.api.Strings;
import cynoodle.core.discord.MemberKey;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes similarity for the last and current message,
 * using {@link cynoodle.core.api.Strings#similarity(String, String)}.
 *
 * 0 % = 0% similarity or lev. distance is over 32
 * 100 % = 100% similarity
 */
public final class LastSimilarityAnalyzer implements SpamAnalyzer {
    LastSimilarityAnalyzer() {}

    private static final int limit = 32;

    private final Map<MemberKey, String> tracker = new HashMap<>();

    @Override
    public double analyze(@Nonnull GuildMessageReceivedEvent event) {

        MemberKey key = MemberKey.of(event.getMember());

        String content = event.getMessage().getContentRaw();
        String last = this.tracker.getOrDefault(key, null);

        double score;

        if(last == null) score = 0d;
        else score = Strings.similarity(content, last, limit);

        this.tracker.put(key, content);

        return score;
    }
}
