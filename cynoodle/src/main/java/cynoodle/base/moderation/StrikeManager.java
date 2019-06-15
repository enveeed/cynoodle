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

package cynoodle.base.moderation;

import cynoodle.discord.DiscordPointer;
import cynoodle.discord.MEntityManager;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * Manager for {@link Strike Strikes}.
 */
public final class StrikeManager {

    private final MEntityManager<Strike> entities;

    // ===

    StrikeManager(@Nonnull MEntityManager<Strike> entities) {
        this.entities = entities;
    }

    // ===

    @Nonnull
    public Strike create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user,
                         @Nonnull String reason) {
        return this.entities.create(guild, user, strike -> {
            strike.setReason(reason);
        });
    }

    @Nonnull
    public Strike create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user,
                         @Nonnull String reason, @Nonnull Decay decay) {
        return this.entities.create(guild, user, strike -> {
            strike.setReason(reason);
            strike.setDecay(decay);
        });
    }

    @Nonnull
    public Strike create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user,
                         @Nonnull String reason, @Nonnull Decay decay, @Nonnull Instant timestamp) {
        return this.entities.create(guild, user, strike -> {
            strike.setReason(reason);
            strike.setDecay(decay);
            strike.setTimestamp(timestamp);
        });
    }

    // ===

    @Nonnull
    public Stream<Strike> all(@Nonnull DiscordPointer guild) {
        return this.entities.stream(Strike.filterGuild(guild));
    }

    @Nonnull
    public Stream<Strike> allOfMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return this.entities.stream(Strike.filterMember(guild, user));
    }
}
