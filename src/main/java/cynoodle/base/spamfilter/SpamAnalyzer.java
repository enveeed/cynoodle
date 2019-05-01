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
