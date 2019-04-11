/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.Stream;

import static cynoodle.core.base.xp.XPChecks.validLevel;
import static cynoodle.core.base.xp.XPChecks.validRankName;

/**
 * Manager for {@link Rank Ranks}.
 */
public final class RankManager {

    private final GEntityManager<Rank> entities;

    // ===

    RankManager(@Nonnull GEntityManager<Rank> entities) {
        this.entities = entities;
    }

    // ===

    /**
     * Get a Rank by ID.
     * @param id the id
     * @return optional containing the rank or empty
     */
    @Nonnull
    public Optional<Rank> get(long id) {
        return this.entities.get(id);
    }

    //

    @Nonnull
    public Optional<Rank> getOnLevel(int level) {
        validLevel(level);
        return this.entities.first(Rank.filterLevel(level));
    }

    //

    /**
     * Create a new Rank at the given Guild.
     * @param guild the guild
     * @param name the name of the new rank
     * @param level the level of the new rank
     * @return the new Rank
     */
    @Nonnull
    public Rank create(@Nonnull DiscordPointer guild, @Nonnull String name, int level) {

        validRankName(name);
        validLevel(level);

        if(existsOnLevel(level))
            throw new IllegalArgumentException("There is already a Rank for this level: " + level);

        return this.entities.create(guild, rank -> {
            rank.setName(name);
            rank.setLevel(level);
        });
    }

    //

    /**
     * Get all Ranks at the given Guild.
     * @param guild the guild
     * @return a stream of all ranks
     */
    @Nonnull
    public Stream<Rank> all(@Nonnull DiscordPointer guild) {
        return this.entities.stream(guild);
    }

    // ===

    /**
     * Check if there is a Rank for the given level.
     * @param level the level
     * @return true if there is a rank, false otherwise
     */
    public boolean existsOnLevel(int level) {
        validLevel(level);
        return this.entities.exists(Rank.filterLevel(level));
    }
}
