/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
