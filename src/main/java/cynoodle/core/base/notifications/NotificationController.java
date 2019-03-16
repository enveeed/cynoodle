/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.Optional;

// TODO see TODOs
public final class NotificationController {
    NotificationController() {}

    private final NotificationsModule module = Module.get(NotificationsModule.class);

    private final GEntityManager<NotificationSettings> settingsManager =
            module.getSettingsManager();

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

            NotificationType type = notification.getType();
            NotificationSettings settings = settingsManager.firstOrCreate(this.guild);

            // CHANNEL

            DiscordPointer channelP = null;

            // TODO respect channel settings, not just context

            Optional<DiscordPointer> contextResult = notification.getContext();
            if(contextResult.isPresent()) channelP = contextResult.orElseThrow();

            if(channelP == null) {
                // TODO warn that notification was discarded cause no channel was set
                return;
            }

            TextChannel channel;

            Optional<TextChannel> channelResult = channelP.asTextChannel();
            if(channelResult.isPresent()) channel = channelResult.orElseThrow();
            else {
                // TODO warn that notification was discarded cause channel was invalid
                return;
            }

            // MESSAGE

            // TODO replace with name based variables (not index)
            // TODO use defined message instead of temporary fallback

            String message = type.getFallback();

            String out = MessageFormat.format(message, (Object[]) notification.getVariables());

            // NOTIFY

            // TODO check permissions

            channel.sendMessage(out).queue();
        }

    }

}
