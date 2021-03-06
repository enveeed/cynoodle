/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.spamfilter;

import com.google.common.eventbus.Subscribe;
import cynoodle.core.base.notifications.NotificationType;
import cynoodle.core.base.notifications.NotificationTypeRegistry;
import cynoodle.core.base.notifications.NotificationsModule;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.entities.NestedEntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;

@MIdentifier("base:spamfilter")
@MRequires("base:notifications")
public final class SpamFilterModule extends Module {
    private SpamFilterModule() {}

    final static EntityType<SpamFilterSettings>         ENTITY_SETTINGS         = EntityType.of(SpamFilterSettings.class);
    final static NestedEntityType<SpamAnalyzerSettings> SUB_ANALYZER_SETTINGS   = NestedEntityType.of(SpamAnalyzerSettings.class);

    final static NotificationType NOTIFICATION_MUTED
            = NotificationType.of("base:spamfilter:muted", "member", "score");

    // ===

    private GEntityManager<SpamFilterSettings> settingsManager;

    private SpamAnalyzerRegistry registry;
    private SpamFilterCache cache;

    private SpamFilterController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        this.settingsManager = new GEntityManager<>(ENTITY_SETTINGS);

        this.registry = new SpamAnalyzerRegistry();
        this.cache = new SpamFilterCache();

        this.controller = new SpamFilterController();

        //

        this.registry.register("base.character_chain", new CharacterChainAnalyzer());
        this.registry.register("base.mention_count", new MentionCountAnalyzer());

        this.registry.register("base.last_rate", new LastRateAnalyzer());
        this.registry.register("base.last_similarity", new LastSimilarityAnalyzer());

        //this.registry.register("base.last_equality", new EqualityAnalyzer());

        //

        NotificationTypeRegistry notificationRegistry = Module.get(NotificationsModule.class)
                .getRegistry();

        notificationRegistry.register(NOTIFICATION_MUTED);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Subscribe
    private void onEvent(@Nonnull DiscordEvent event) {
        if(event.is(GuildMessageReceivedEvent.class)) {
            controller().handle(event.get(GuildMessageReceivedEvent.class));
        }
    }

    // ===

    @Nonnull
    public GEntityManager<SpamFilterSettings> getSettingsManager() {
        return this.settingsManager;
    }

    //

    @Nonnull
    public SpamAnalyzerRegistry getRegistry() {
        return this.registry;
    }

    @Nonnull
    SpamFilterCache getCache() {
        return this.cache;
    }

    //

    @Nonnull
    public SpamFilterController controller() {
        return this.controller;
    }
}
