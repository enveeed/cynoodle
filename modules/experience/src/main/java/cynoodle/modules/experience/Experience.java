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

package cynoodle.modules.experience;

import com.mongodb.client.model.Filters;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MChecks;
import cynoodle.discord.MEntityManager;
import cynoodle.module.Module;
import cynoodle.util.Random;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Experience {
    Experience() {}

    public static final long MIN_VALUE = 0;
    public static final long MAX_VALUE = 1_000_000_000_000_000_000L;

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = ExperienceFormula.getReachedLevel(MAX_VALUE);

    // ===

    final GEntityManager<ExperienceSettings> settingsEntityManager
            = new GEntityManager<>(ExperienceSettings.TYPE);
    final MEntityManager<ExperienceStatus> statusEntityManager
            = new MEntityManager<>(ExperienceStatus.TYPE);
    final GEntityManager<Rank> rankEntityManager
            = new GEntityManager<>(Rank.TYPE);

    final RankCalculator rankCalculator
            = new RankCalculator();

    // ===

    @Nonnull
    public ExperienceSettings getSettings(@Nonnull Guild guild) {
        return settingsEntityManager.firstOrCreate(guild);
    }

    @Nonnull
    public ExperienceStatus getStatus(@Nonnull Member member) {
        return statusEntityManager.firstOrCreate(member);
    }

    // === RANKS ===

    @Nonnull
    public Set<Rank> getRanks(@Nonnull Guild guild) {
        return rankEntityManager.stream(guild)
                .collect(Collectors.toUnmodifiableSet());
    }

    //

    @Nonnull
    public Optional<Rank> findRank(@Nonnull Guild guild, int level) {
        return rankEntityManager.first(Filters.and(
                Rank.filterGuild(guild),
                Rank.filterLevel(level)));
    }

    //

    @Nonnull
    public Rank createRank(@Nonnull Guild guild, int level, @Nonnull String name) {
        // ensure level not already exists
        if(findRank(guild, level).isPresent())
            throw new IllegalArgumentException("There is already a Rank at the given level: " + level);

        return rankEntityManager.create(guild, rank -> rank.create(level, name));
    }

    //

    @Nonnull
    public RankCalculator getRankCalculator() {
        return this.rankCalculator;
    }

    // === GAIN ===

    /**
     * Gain experience for the given member of the given type.
     * @param member the member
     * @param gainType the gain type
     */
    public void gain(@Nonnull Member member, @Nonnull GainType gainType) {
        MChecks.notBot(member);

        GainSettings settings = settingsEntityManager.firstOrCreate(member.getGuild()).getGainSettings(gainType);

        if(!settings.isEnabled()) return; // this GainType is disabled

        ExperienceStatus status = statusEntityManager.firstOrCreate(member);

        if(!status.isTimeoutExpired(gainType)) return; // timed out

        long min = settings.getMinimumValue();
        long max = settings.getMaximumValue();

        long value = Random.nextLong(min, max);

        status.modifyValue(operand -> operand + value);
        status.setTimeoutDuration(gainType, settings.getTimeout());

        status.persist();
    }

    // ===

    @Nonnull
    public static Experience get() {
        return Module.get(ExperienceModule.class)
                .getExperience();
    }
}
