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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spam analyzer which finds user mentions.
 *
 * 0 % = 0 mentions
 * 100 % = 10 mentions
 */
// TODO improvements / offset ?
public final class MentionCountAnalyzer implements SpamAnalyzer {
    MentionCountAnalyzer() {}

    private final Pattern pattern = Pattern.compile("<@!?(\\d+)>");

    @Override
    public double analyze(@Nonnull GuildMessageReceivedEvent event) {

        String content = event.getMessage().getContentRaw();

        Matcher matcher = pattern.matcher(content);

        int count = 0;
        while (matcher.find())
            count++;

        return (double) count / 10d;
    }
}
