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

package cynoodle.modules.experience;

import com.google.common.eventbus.Subscribe;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Event handler for {@link Experience}.
 */
public final class ExperienceEventHandler {
    ExperienceEventHandler() {}

    // ===

    @Subscribe
    public void onMessage(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.isWebhookMessage()) return;

        Experience experience = Experience.get();

        // gain for message
        experience.gain(Objects.requireNonNull(event.getMember()), GainType.MESSAGE);

        // gain for (possible) attachment
        if(event.getMessage().getAttachments().size() > 0)
            experience.gain(event.getMember(), GainType.ATTACHMENT);
    }

    public void onReact(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getUser().isBot()) return;

        Experience experience = Experience.get();

        // gain for reaction
        experience.gain(event.getMember(), GainType.REACTION);
    }
}
