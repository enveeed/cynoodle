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

package cynoodle.discord;

import cynoodle.entity.IEntity;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * superinterface for public interfaces to {@link UEntity}.
 */
public interface IUEntity extends IEntity {

    /**
     * Get an optional containing a pointer to the User,
     * if one is set, otherwise empty
     * @return user pointer optional
     */
    @Nonnull
    Optional<UReference> getUser();

    /**
     * Attempt to get a pointer to the User, throws if empty.
     * @return user pointer
     * @throws IllegalStateException if there is no user set
     */
    @Nonnull
    default UReference requireUser() throws IllegalStateException {
        return getUser().orElseThrow(() -> new IllegalStateException("No User set."));
    }

    /**
     * Attempt to get the ID of the User, throws if empty.
     * @return user ID
     * @throws IllegalStateException if there is no user set
     */
    default long getUserID() throws IllegalStateException {
        return requireUser().getID();
    }

    //

    /**
     * Set the user to the given pointer.
     * @param user the user
     */
    void setUser(@Nullable UReference user);

    /**
     * Set the user to the given User.
     * @param user the user
     */
    default void setUser(@Nullable User user) {
        setUser(user == null ? null : UReference.to(user));
    }

    //

    default boolean hasUser() {
        return getUser().isPresent();
    }

}
