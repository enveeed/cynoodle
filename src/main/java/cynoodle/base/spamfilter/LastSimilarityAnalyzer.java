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

import cynoodle.api.Strings;
import cynoodle.discord.MemberKey;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes similarity for the last and current message,
 * using {@link cynoodle.api.Strings#similarity(String, String)}.
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
