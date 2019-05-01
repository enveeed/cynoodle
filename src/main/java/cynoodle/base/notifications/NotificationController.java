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

package cynoodle.base.notifications;

import cynoodle.api.Random;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

// TODO see TODOs
public final class NotificationController {
    NotificationController() {}

    private final NotificationsModule module = Module.get(NotificationsModule.class);

    private final GEntityManager<NotificationSettings> settingsManager =
            module.getSettingsManager();
    private final NotificationTypeRegistry registry =
            module.getRegistry();

    // ===

    @Nonnull
    public OnGuild onGuild(@Nonnull DiscordPointer guild) {
        return new OnGuild(guild);
    }

    // ===

    public final class OnGuild {

        private final DiscordPointer guild;

        // ===

        private OnGuild(@Nonnull DiscordPointer guild) {
            this.guild = guild;
        }

        // ===

        public void emit(@Nonnull Notification notification) {

            NotificationSettings settings       = settingsManager.firstOrCreate(this.guild);
            NotificationProperties properties   = settings.getOrCreateProperties(notification.getIdentifier());
            NotificationType type               = registry.find(notification.getIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Can't emit notification of unknown type: " + notification.getIdentifier()));

            //

            if(!properties.isEnabled()) return;

            //

            Set<String> messages = properties.getMessages();
            if(messages.size() == 0) return; // TODO warn that its enabled but there are no messages ?

            String message = Random.nextOf(messages);

            //

            String out = notification.format(message, type.getVariableNames(), notification.getVariables());

            //

            // 1. context 2. channel 3. fail
            DiscordPointer channelP = notification.getContext()
                    .or(properties::getChannel).orElseThrow(() -> new IllegalStateException("No channel set for notification "
                            + notification.getIdentifier() + " on guild " + guild + ", it had no context either!"));
            TextChannel channel = channelP.asTextChannel()
                    .orElseThrow();// TODO warn unknown channel and discard instead

            //

            channel.sendMessage(out).queue();
        }

        public void emit(@Nonnull String identifier, @Nullable DiscordPointer context, @Nonnull String... variables) {
            emit(Notification.of(identifier, context, variables));
        }

        public void emit(@Nonnull String identifier, @Nonnull String... variables) {
            emit(identifier, null, variables);
        }

    }

}
