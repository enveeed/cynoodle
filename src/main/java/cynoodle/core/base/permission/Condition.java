/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.permission;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEmbed;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * A Condition is a functional {@link GEmbed} which checks
 * if a guild member matches an implementation dependent condition.
 */
public abstract class Condition extends GEmbed {

    // TODO other properties and / or methods

    /**
     * Check if the given user meets this condition.
     * @param user the user
     * @return true if the condition is met, otherwise false
     */
    public abstract boolean check(@Nonnull DiscordPointer user);

    // ===

    /**
     * Get a predicate representing this condition.
     * @return a predicate for this condition
     */
    @Nonnull
    public final Predicate<DiscordPointer> asPredicate() {
        return this::check;
    }
}
