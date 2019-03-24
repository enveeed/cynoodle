/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.Numbers;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
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

        LOG.atFinest().log("Analyzing %s from %s", event.getMessage(), user);

        double delta = 0d;

        for (Map.Entry<String, SpamAnalyzer> entry : registry.all()) {

            String identifier = entry.getKey();
            SpamAnalyzer analyzer = entry.getValue();

            SpamAnalyzerSettings analyzerSettings = settings.getOrCreateAnalyzerSettings(identifier);

            double result = analyzer.analyze(event);
            double adjustedResult = result * analyzerSettings.getIntensity();

            LOG.atFinest().log("Analyzer %s: %s%% (adjusted %s%%)",
                    identifier,
                    Numbers.format(result * 100, 0),
                    Numbers.format(adjustedResult * 100, 0));

            delta += adjustedResult;
        }

        if(delta == 0) return; // there was no spam found, so there is no need to handle it

        //

        double modified = this.cache.modify(guild, user, delta);

        LOG.atFinest().log("analyzing finished: + %s >> %s ",
                Numbers.format(delta, 2),
                Numbers.format(modified, 2));

        // take action

        double threshold = settings.getMuteThreshold();

        if(modified >= threshold) {
            LOG.atFinest().log("Threshold %s was reached, member should be muted! (TODO)",
                    Numbers.format(threshold,2));
            // TODO mute member
        }
    }
}
