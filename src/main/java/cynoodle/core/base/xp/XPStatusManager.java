/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.discord.DiscordPointer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

// TODO improvements
final class XPStatusManager {
    XPStatusManager() {}

    // ===

    private final Map<DiscordPointer, XPStatus> status = new HashMap<>();

    // ===

    @Nonnull
    public XPStatus get(@Nonnull DiscordPointer guild) {
        return this.status.computeIfAbsent(guild, XPStatus::new);
    }

}
