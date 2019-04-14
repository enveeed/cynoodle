/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

public interface SpamAnalyzer {

    /**
     * Analyze the given event and return the spam value for it, between 0 and 1.
     * If the event is very unlikely to be spam or if the message contains 0% spam, zero should be returned.
     * If the event is very likely to be spam or if the message contains 100% spam, one should be returned.
     * @param event the event to analyze
     * @return the spam value for the event, between 0 and 1.
     */
    double analyze(@Nonnull GuildMessageReceivedEvent event);

}
