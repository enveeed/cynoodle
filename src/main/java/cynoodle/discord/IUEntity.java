/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.discord;

import cynoodle.entities.IEntity;
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
    Optional<DiscordPointer> getUser();

    /**
     * Attempt to get a pointer to the User, throws if empty.
     * @return user pointer
     * @throws IllegalStateException if there is no user set
     */
    @Nonnull
    default DiscordPointer requireUser() throws IllegalStateException {
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
    void setUser(@Nullable DiscordPointer user);

    /**
     * Set the user to the given User.
     * @param user the user
     */
    default void setUser(@Nullable User user) {
        setUser(user == null ? null : DiscordPointer.to(user));
    }

    //

    default boolean hasUser() {
        return getUser().isPresent();
    }

}
