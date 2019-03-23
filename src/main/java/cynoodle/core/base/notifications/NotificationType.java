/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import javax.annotation.Nonnull;

// TODO meta / improvements / builder / immutability?
public final class NotificationType {

    private final String identifier;

    private final String[] variableNames;

    // ===

    private NotificationType(@Nonnull String identifier, @Nonnull String[] variableNames) {
        this.identifier = identifier;
        this.variableNames = variableNames;
    }

    // ===

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public String[] getVariableNames() {
        return this.variableNames;
    }

    //

    public int getVariableCount() {
        return this.variableNames.length;
    }

    // ===

    @Nonnull
    public static NotificationType of(@Nonnull String identifier, @Nonnull String... variableNames) {
        return new NotificationType(identifier, variableNames);
    }
}
