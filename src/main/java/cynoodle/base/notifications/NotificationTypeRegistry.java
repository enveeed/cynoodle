/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.notifications;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NotificationTypeRegistry {
    NotificationTypeRegistry() {}

    private final Map<String, NotificationType> types = new HashMap<>();

    // ===

    @Nonnull
    public Optional<NotificationType> find(@Nonnull String identifier) {
        return Optional.ofNullable(this.types.get(identifier));
    }

    // ===

    public void register(@Nonnull NotificationType type) {
        this.types.put(type.getIdentifier(), type);
    }

}
