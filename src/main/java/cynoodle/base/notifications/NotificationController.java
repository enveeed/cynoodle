/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
