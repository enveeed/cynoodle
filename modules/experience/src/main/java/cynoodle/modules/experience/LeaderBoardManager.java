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
import cynoodle.discord.GReference;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public final class LeaderBoardManager {
    LeaderBoardManager() {}

    // ===

    private final Map<GReference, LeaderBoard> cache = new HashMap<>();

    // ===

    /**
     * Attempt to get a guilds LeaderBoard if existent.
     * @param guild the guild
     * @return an optional containing the LeaderBoard, otherwise empty optional
     */
    @Nonnull
    public Optional<LeaderBoard> get(@Nonnull GReference guild) {
        if(this.cache.containsKey(guild)) {
            LeaderBoard board = this.cache.get(guild);
            if(board.isExpired()) {
                // NOTE: we keep it in cache here so getOrExpired can use that as well
                return Optional.empty();
            }
            else return Optional.of(board);
        }
        else return Optional.empty();
    }

    @Nonnull
    public Optional<LeaderBoard> getOrExpired(@Nonnull GReference guild) {
        return Optional.ofNullable(this.cache.get(guild));
    }

    // ===

    @Nonnull
    private LeaderBoard generate(@Nonnull GReference guild) {
        Experience experience = Experience.get();

        // 1: generate filter to include all CURRENT members
        Set<Bson> filters = guild.requireGuild().getMembers().stream()
                .map(ExperienceStatus::filterMember)
                .collect(Collectors.toSet());

        // 2: query xp status, sorted
        List<ExperienceStatus> statusList = experience.statusEntityManager.stream(Filters.or(filters))
                .sorted(ExperienceStatus.orderDescending())
                .collect(Collectors.toList());

        // 3: create new board instance, timestamp of now
        return LeaderBoard.ofDescendingStatus(statusList);
    }
}
