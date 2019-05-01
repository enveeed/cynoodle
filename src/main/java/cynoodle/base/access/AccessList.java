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

package cynoodle.base.access;

import cynoodle.discord.DiscordPointer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * An {@link AccessList} contains cascading
 * allow / neutral / deny permissions for roles and members.
 */
public interface AccessList {

    /**
     * Get all {@link RoleEntry RoleEntries}.
     * @return unmodifiable collection of all entries for roles
     */
    @Nonnull
    Collection<RoleEntry> getRoles();

    /**
     * Get all {@link UserEntry UserEntries}.
     * @return unmodifiable collection of all entries for users
     */
    @Nonnull
    Collection<UserEntry> getUsers();

    //

    @Nonnull
    Optional<RoleEntry> forRole(@Nonnull DiscordPointer role);

    @Nonnull
    Optional<UserEntry> forUser(@Nonnull DiscordPointer user);

    @Nonnull
    RoleEntry forRoleOrCreate(@Nonnull DiscordPointer role);

    @Nonnull
    UserEntry forUserOrCreate(@Nonnull DiscordPointer user);

    //

    void removeRole(@Nonnull DiscordPointer role);

    void removeUser(@Nonnull DiscordPointer user);

    //

    /**
     * Get the default status.
     * @return the default status
     */
    @Nonnull
    Status getDefaultStatus();

    /**
     * Set the default status.
     * @param defaultStatus the default status.
     */
    void setDefaultStatus(@Nonnull Status defaultStatus);

    // ===

    /**
     * Test the access list for the effective status of the given user.
     * </p>
     * If there is a {@link UserEntry} for the user and it is ALLOW or DENY, this will be the result.
     * If there is none, or if its NEUTRAL, the {@link RoleEntry RoleEntries} will be queried top-down (up to @everyone)
     * until a ALLOW or DENY entry is found. If neither was found, {@link #getDefaultStatus()} will be the result.
     * @param user the user
     * @return the effective permission status in this access list for the user.
     */
    @Nonnull
    Status check(@Nonnull DiscordPointer user);

    /**
     * Check if the user has access. This is defined as
     * their effective status being {@link Status#ALLOW} or {@link Status#NEUTRAL}.
     * @param user the user
     * @return true if the user has access.
     */
    boolean checkAccess(@Nonnull DiscordPointer user);

    // ===

    /**
     * NEUTRAL / DENY / ALLOW
     */
    enum Status {

        /**
         * NEUTRAL: Don't affect anything (0)
         */
        NEUTRAL,
        /**
         * DENY: Deny this permission. (1)
         */
        DENY,
        /**
         * ALLOW: Allow this permission. (2)
         */
        ALLOW,

    }

    // ===

    interface RoleEntry {

        @Nonnull
        DiscordPointer getRole();

        @Nonnull
        Status getStatus();

        void setStatus(@Nonnull Status flag);

    }

    interface UserEntry {

        @Nonnull
        DiscordPointer getUser();

        @Nonnull
        Status getStatus();

        void setStatus(@Nonnull Status flag);

    }
}
