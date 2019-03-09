/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.condition;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.entities.embed.Embeddable;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A Condition is a functional {@link Embeddable} which checks
 * if a guild member matches an implementation dependent condition.
 */
public abstract class Condition extends Embeddable {

    // ===

    /**
     * Check if the given user meets this condition.
     * @param guild the guild
     * @param user the user
     * @return true if the condition is met, otherwise false
     */
    public abstract boolean check(@Nonnull DiscordPointer guild,
                                  @Nonnull DiscordPointer user);

    // ===

    public final boolean checkAll(@Nonnull DiscordPointer guild,
                                  @Nonnull Collection<DiscordPointer> users) {
        for (DiscordPointer user : users) if(!check(guild, user)) return false;
        return true;
    }
}
