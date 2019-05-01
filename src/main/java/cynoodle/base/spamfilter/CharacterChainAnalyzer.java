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
 * Spam analyzer which finds chained characters
 * (chained = 3+ equal chars right next after each other)
 *
 * 0% = repeated characters are 0% of the message
 * 100% = repeated characters are 100% of the message
 */
public final class CharacterChainAnalyzer implements SpamAnalyzer {
    CharacterChainAnalyzer() {}

    private final Pattern pattern = Pattern.compile("(.)\\1{2,}");

    @Override
    public double analyze(@Nonnull GuildMessageReceivedEvent event) {

        String content = event.getMessage().getContentRaw();

        if(content.length() == 0) return 0d;

        Matcher matcher = pattern.matcher(content);

        int count = 0;

        while (matcher.find()) {
            int repeated = matcher.end() - matcher.start();
            count += repeated;
        }

        return (double) count / content.length();
    }
}
