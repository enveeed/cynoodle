/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Entity or Embed which holds a User.
 * @see UEntity
 */
public interface UHolder {

    @Nonnull
    Optional<DiscordPointer> getUser();

    void setUser(@Nullable DiscordPointer user);

}
