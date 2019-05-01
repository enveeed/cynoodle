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

package cynoodle.base.commands;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.api.Checks;
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
