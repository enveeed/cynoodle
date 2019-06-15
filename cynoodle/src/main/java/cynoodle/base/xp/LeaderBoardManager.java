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

package cynoodle.base.xp;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.discord.DiscordPointer;
import net.dv8tion.jda.api.entities.Guild;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Manager for {@link LeaderBoard LeaderBoards}.
 */
public final class LeaderBoardManager {

    private final MutableLongObjectMap<LeaderBoard> boards = new LongObjectHashMap<>();

    // ===

    LeaderBoardManager() {}

    // ===

    /**
     * Attempt to get a guilds LeaderBoard if existent.
     * @param guild the guild
     * @return an optional containing the LeaderBoard, otherwise empty optional
     */
    @Nonnull
    public Optional<LeaderBoard> get(@Nonnull DiscordPointer guild) {
        if(this.boards.containsKey(guild.getID())) {
            LeaderBoard board = this.boards.get(guild.getID());
            if(board.isExpired()) {
                this.boards.remove(guild.getID());
                return Optional.empty();
            }
            else return Optional.of(board);
        }
        else return Optional.empty();
    }

    // ===

    /**
     * Generate the leader board for the given guild, replacing
     * the current one if existent.
     * @param guild the guild
     * @return the generated leader board
     */
    @Nonnull
    @CanIgnoreReturnValue
    public LeaderBoard generate(@Nonnull Guild guild) {

        // generate the board
        LeaderBoard board = LeaderBoard.generate(guild);

        // store the board
        this.boards.put(guild.getIdLong(), board);

        return board;
    }
}
