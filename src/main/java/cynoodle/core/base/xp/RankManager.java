/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.mongodb.client.model.Filters;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;

import javax.annotation.Nonnull;
import java.util.Comparator;
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

    /**
     * Get the Rank which is set to the given level.
     * @param guild the guild
     * @param level the level
     * @return the rank, or empty if there is none on the level
     */
    @Nonnull
    public Optional<Rank> getAtLevel(@Nonnull DiscordPointer guild, int level) {
        validLevel(level);
        return this.entities.first(Filters.and(Rank.filterGuild(guild), Rank.filterLevel(level)));
    }

    /**
     * Get a Rank which is effective at the given level.
     * @param guild the guild
     * @param level the level
     * @return the rank, or empty if there is none effective for the level
     */
    @Nonnull
    public Optional<Rank> getAtLevelEffective(@Nonnull DiscordPointer guild, int level) {
        validLevel(level);
        return this.entities.stream(Filters.and(Rank.filterGuild(guild), Rank.filterLevelAndPrevious(level)))
                .max(Comparator.comparingInt(Rank::getLevel));
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
