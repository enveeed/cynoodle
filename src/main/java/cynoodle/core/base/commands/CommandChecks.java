/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.core.api.Checks;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Validity checks for single command values, to be used within {@link Command Commands},
 * similar to {@link Checks}. Throws {@link CommandError CommandErrors}
 * instead of {@link IllegalArgumentException IllegalArgumentExceptions}.
 */
public final class CommandChecks {
    private CommandChecks() {}

    // === USER ===

    @Nonnull
    @CanIgnoreReturnValue
    public User notBot(@Nonnull User user) throws CommandError {
        if(user.isBot()) throw CommandErrors.simple("User cannot be a Bot!");
        return user;
    }
}
