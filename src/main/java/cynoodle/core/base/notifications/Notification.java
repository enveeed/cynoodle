/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.DiscordPointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Immutable notification.
 */
public final class Notification {

    private final NotificationType type;

    private final DiscordPointer context;
    private final String[] variables;

    // ===

    Notification(@Nonnull NotificationType type, @Nullable DiscordPointer context, @Nonnull String[] variables) {
        this.type = type;
        this.context = context;
        this.variables = variables;
    }

    // ===

    @Nonnull
    public NotificationType getType() {
        return this.type;
    }

    //

    @Nonnull
    public Optional<DiscordPointer> getContext() {
        return Optional.ofNullable(this.context);
    }

    @Nonnull
    public String[] getVariables() {
        return this.variables;
    }

}
