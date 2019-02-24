/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.mongodb.client.model.Filters;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Manager for {@link Rank Ranks}.
 */
public final class RankManager extends GEntityManager<Rank> {

    RankManager(@Nonnull EntityType<Rank> type) {
        super(type);
    }

    // ===

    @Nonnull
    public Rank create(@Nonnull DiscordPointer guild, @Nonnull String name, int level) {
        return create(guild, rank -> rank.create(name, level));
        // NOTE: rank.create() is going to fail when the level already exists,
        // that's why there is no need for an additional check
    }

    // ===

    /**
     * Check if there is a Rank which matches the given level exactly.
     * @param guild the guild
     * @param level the level
     * @return true if it exists, otherwise false
     */
    public boolean existsByLevel(@Nonnull DiscordPointer guild, int level) {
        return exists(Filters.and(Rank.filterGuild(guild), Rank.filterLevel(level)));
    }

    // ===

    /**
     * Find the first rank which matches the given level exactly.
     * @param guild the guild
     * @param level the level
     * @return an optional containing the rank if found
     * @see Rank#filterLevel(int)
     */
    @Nonnull
    public Optional<Rank> firstByLevel(@Nonnull DiscordPointer guild, int level) {
        return first(Filters.and(Rank.filterGuild(guild), Rank.filterLevel(level)));
    }
}
