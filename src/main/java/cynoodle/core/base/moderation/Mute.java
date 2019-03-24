/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MemberKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Immutable Mute for a member.
 * Either finite (with a duration) or infinite (without a duration).
 */
public final class Mute {

    private final MemberKey member;

    private final Instant timestamp;
    private final Duration duration;

    // ===

    private Mute(@Nonnull MemberKey member, @Nonnull Instant timestamp, @Nullable Duration duration) {
        this.member = member;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    // ===

    @Nonnull
    public DiscordPointer getGuild() {
        return member.getGuild();
    }

    @Nonnull
    public DiscordPointer getUser() {
        return member.getUser();
    }

    //

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(this.duration);
    }

    //

    public boolean isInfinite() {
        return this.duration == null;
    }

    public boolean isFinite() {
        return this.duration != null;
    }

    //

    @Nonnull
    public Optional<Instant> getExpiryTimestamp() {
        if(isInfinite()) return Optional.empty();
        else return Optional.of(getTimestamp().plus(getDuration().orElseThrow()));
    }

    //

    public boolean isExpired() {
        if(isInfinite()) return false;
        else {
            Instant now = Instant.now();
            Instant expiry = getExpiryTimestamp().orElseThrow();
            return now.isAfter(expiry);
        }
    }

    public boolean isEffective() {
        if(isInfinite()) return true;
        else {
            Instant now = Instant.now();
            Instant expiry = getExpiryTimestamp().orElseThrow();
            return now.isBefore(expiry);
        }
    }

    // ===

    @Nonnull
    public static Mute infinite(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return new Mute(MemberKey.of(guild, user), Instant.now(), null);
    }

    @Nonnull
    public static Mute finite(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user, @Nonnull Duration duration) {
        return new Mute(MemberKey.of(guild, user), Instant.now(), duration);
    }

}
