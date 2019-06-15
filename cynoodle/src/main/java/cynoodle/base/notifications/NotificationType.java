/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.notifications;

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
