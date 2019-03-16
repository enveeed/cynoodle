/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.DiscordPointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO meta / improvements / builder / immutability?
public final class NotificationType {

    private final String identifier;
    private final String fallback; // TODO temporary / externalize

    // ===

    public NotificationType(String identifier, String fallback) {
        this.identifier = identifier;
        this.fallback = fallback;
    }

    //

    public String getIdentifier() {
        return identifier;
    }

    public String getFallback() {
        return fallback;
    }

    // ===

    @Nonnull
    public Notification create(@Nullable DiscordPointer context, @Nonnull String... variables) {
        return new Notification(this, context, variables);
    }

    @Nonnull
    public Notification create(@Nonnull String... variables) {
        return new Notification(this, null, variables);
    }

}
