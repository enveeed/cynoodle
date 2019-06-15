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

import cynoodle.discord.MemberKey;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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

        long time = event.getMessage().getTimeCreated()
                .toInstant().toEpochMilli();
        long last = this.tracker.getOrDefault(key, -1L);

        double score;

        if(last == -1L) score = 0d;
        else score = (1d / (time - last)) * 100;

        this.tracker.put(key, time);

        return score;
    }
}
