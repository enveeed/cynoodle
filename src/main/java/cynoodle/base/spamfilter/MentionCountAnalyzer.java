/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
