/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

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
