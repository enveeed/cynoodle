/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.core.discord.DiscordPointer;
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
