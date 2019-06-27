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

package cynoodle.test.xp;

import com.mongodb.client.model.Filters;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static cynoodle.test.xp.XPChecks.validLevel;
import static cynoodle.test.xp.XPChecks.validRankName;

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
