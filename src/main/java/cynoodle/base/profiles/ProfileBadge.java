/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.profiles;

import cynoodle.discord.DiscordPointer;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class ProfileBadge {

    private final DiscordPointer emote;
    private final Predicate<User> predicate;

    // ===

    public ProfileBadge(@Nonnull DiscordPointer emote, @Nonnull Predicate<User> predicate) {
        this.emote = emote;
        this.predicate = predicate;
    }

    // ===

    @Nonnull
    public DiscordPointer getEmote() {
        return this.emote;
    }

    // ===

    public boolean test(@Nonnull User user) {
        return this.predicate.test(user);
    }
}
