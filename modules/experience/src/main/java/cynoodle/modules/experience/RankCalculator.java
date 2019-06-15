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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains methods to accurately calculate what {@link Rank Ranks} a member
 * has at a given level.
 */
public final class RankCalculator {
    RankCalculator() {}

    // ===

    @Nonnull
    public List<Rank> calculateEffectiveRanks(@Nonnull Guild guild, int level) {

        Experience experience = Experience.get();

        // NOTE: Order is the natural order of Ranks, that being the lowest to the highest (also see Rank.compareTo)
        return experience.rankEntityManager
                .stream(Filters.and(
                        Rank.filterGuild(guild),
                        Rank.filterLevelAndPrevious(level)
                ))
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    @Nonnull
    public Set<Role> calculateEffectiveRoles(@Nonnull Guild guild, int level) {

        List<Rank> ranks = calculateEffectiveRanks(guild, level);

        Set<Role> roles = new HashSet<>();

        for (int i = 0; i < ranks.size(); i++) {

            Rank rank = ranks.get(i);

            if(i == (ranks.size() - 1)) {
                // this is the last rank the member has, so add all roles
                for (RankRole rankRole : rank.getRoles()) {
                    if(!rankRole.hasValidRole()) continue;
                    roles.add(rankRole.getRole().requireRole());
                }

            } else {
                // there are more ranks above this one, so only add keep-enabled roles
                for (RankRole rankRole : rank.getRoles()) {
                    if(!rankRole.hasValidRole()) continue;
                    if(rankRole.isKeepEnabled())
                        roles.add(rankRole.getRole().requireRole());
                }
            }
        }

        return Collections.unmodifiableSet(roles);
    }

}
