/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.spamfilter;

import com.google.common.flogger.FluentLogger;
import cynoodle.api.Numbers;
import cynoodle.base.moderation.ModerationController;
import cynoodle.base.moderation.ModerationModule;
import cynoodle.base.notifications.NotificationController;
import cynoodle.base.notifications.NotificationsModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.Members;
import cynoodle.module.Module;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Map;

public final class SpamFilterController {
    SpamFilterController() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    private final SpamFilterModule module =
            Module.get(SpamFilterModule.class);

    private final GEntityManager<SpamFilterSettings> settingsManager =
            module.getSettingsManager();
    private final SpamAnalyzerRegistry registry =
            module.getRegistry();
    private final SpamFilterCache cache =
            module.getCache();

    private final NotificationController notifications =
            Module.get(NotificationsModule.class).controller();
    private final ModerationController moderation =
            Module.get(ModerationModule.class).controller();

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

    }

    // ===

    // TODO move this elsewhere (e.g. OnGuild / OnMember)
    public void handle(@Nonnull GuildMessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return; // TODO replace with self check
        // TODO do not handle command messages...

        DiscordPointer guild = DiscordPointer.to(event.getGuild());
        DiscordPointer user = DiscordPointer.to(event.getAuthor());

        SpamFilterSettings settings = settingsManager.firstOrCreate(guild);

        //

        if(!settings.isEnabled())
            return; // spam filter is not enabled

        //

        double delta = 0d;

        for (Map.Entry<String, SpamAnalyzer> entry : registry.all()) {

            String identifier = entry.getKey();
            SpamAnalyzer analyzer = entry.getValue();

            SpamAnalyzerSettings analyzerSettings = settings.getOrCreateAnalyzerSettings(identifier);

            double result = analyzer.analyze(event);
            double adjustedResult = result * analyzerSettings.getIntensity();

            delta += adjustedResult;
        }

        if(delta == 0) return; // there was no spam found, so there is no need to handle it

        //

        double modified = this.cache.modify(guild, user, delta);

        // take action

        double threshold = settings.getMuteThreshold();

        if(modified >= threshold) {

            ModerationController.OnMember onMember =
                    moderation.onMember(DiscordPointer.to(event.getGuild()), DiscordPointer.to(event.getAuthor()));

            if(!onMember.isMuted()) {
                onMember.muteFinite(Duration.ofMinutes(10));


                notifications.onGuild(DiscordPointer.to(event.getGuild()))
                        .emit("base:spamfilter:muted",
                                DiscordPointer.to(event.getChannel()),
                                Members.formatAt(DiscordPointer.to(event.getGuild())).format(DiscordPointer.to(event.getAuthor())),
                                Numbers.format(modified, 3));
            }
        }
    }
}
