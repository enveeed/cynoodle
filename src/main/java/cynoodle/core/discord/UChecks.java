/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.core.api.Checks;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

public final class UChecks {
    private UChecks() {}

    // ===

    @Nonnull
    @CanIgnoreReturnValue
    public User notBot(User user) {
        Checks.notNull(user, "user");
        if(user.isBot()) throw new IllegalArgumentException("User is Bot!");
        return user;
    }
}
