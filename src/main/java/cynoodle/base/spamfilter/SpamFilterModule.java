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

import com.google.common.eventbus.Subscribe;
import cynoodle.base.notifications.NotificationType;
import cynoodle.base.notifications.NotificationTypeRegistry;
import cynoodle.base.notifications.NotificationsModule;
import cynoodle.discord.DiscordEvent;
import cynoodle.discord.GEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.entities.NestedEntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
