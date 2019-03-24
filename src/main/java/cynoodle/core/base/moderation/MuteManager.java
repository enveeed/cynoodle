/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MemberKey;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Manager for {@link Mute Mutes}.
 */
final class MuteManager {
    MuteManager() {}

    // ===

    private final Map<MemberKey, Mute> mutes = new HashMap<>();

    // ===

    @Nonnull
    public Optional<Mute> get(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        MemberKey key = MemberKey.of(guild, user);
        return Optional.ofNullable(this.mutes.get(key));
    }

    @Nonnull
    public Optional<Mute> getEffective(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return this.get(guild, user).filter(Mute::isEffective);
    }

    //

    @Nonnull
    public Stream<Mute> all() {
        return this.mutes.values().stream();
    }

    @Nonnull
    public Stream<Mute> allEffective() {
        return all().filter(Mute::isEffective);
    }

    // ===

    public void mute(@Nonnull Mute mute) {
        MemberKey key = MemberKey.of(mute.getGuild(), mute.getUser());
        this.mutes.put(key, mute);
    }

    public void unmute(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        MemberKey key = MemberKey.of(guild, user);
        this.mutes.remove(key);
    }
}
