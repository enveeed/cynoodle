/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Optional;

/**
 * Runtime XP status of a Guild.
 */
public final class XPStatus {

    private final XPModule module = Module.get(XPModule.class);

    // ===

    private final DiscordPointer guild;

    private final MutableLongLongMap timeouts = new LongLongHashMap();

    // ===

    XPStatus(@Nonnull DiscordPointer guild) {
        this.guild = guild;
    }

    // ===

    @Nonnull
    public Optional<Instant> getLastGain(@Nonnull DiscordPointer user) {
        if(timeouts.containsKey(user.getID()))
            return Optional.of(Instant.ofEpochMilli(this.timeouts.get(user.getID())));
        else return Optional.empty();
    }

    public void updateLastGain(@Nonnull DiscordPointer user) {
        this.timeouts.put(user.getID(), Instant.now().toEpochMilli());
    }

    // ===

    public boolean isInTimeout(@Nonnull DiscordPointer user) {
        Optional<Instant> gain = getLastGain(user);
        if(gain.isEmpty()) return false;
        else return gain.orElseThrow().plus(settings().getGainTimeout()).isAfter(Instant.now());
    }

    // ===

    @Nonnull
    private XPSettings settings() {
        return this.module.getSettingsManager().firstOrCreate(guild);
    }
}
