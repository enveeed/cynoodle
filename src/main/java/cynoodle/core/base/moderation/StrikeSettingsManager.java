/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;

import javax.annotation.Nonnull;

/**
 * Manager for {@link StrikeSettings}.
 */
public final class StrikeSettingsManager {

    private final GEntityManager<StrikeSettings> entities;

    // ===

    StrikeSettingsManager(@Nonnull GEntityManager<StrikeSettings> entities) {
        this.entities = entities;
    }

    // ===

    @Nonnull
    public StrikeSettings forGuild(@Nonnull DiscordPointer guild) {
        return this.entities.firstOrCreate(guild);
    }
}
