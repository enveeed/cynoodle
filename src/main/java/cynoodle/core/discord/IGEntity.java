/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.entities.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * superinterface for public interfaces to {@link GEntity}.
 */
public interface IGEntity extends IEntity {

    @Nonnull
    Optional<DiscordPointer> getGuild();

    void setGuild(@Nullable DiscordPointer guild);

    @Nonnull
    default DiscordPointer requireGuild() throws IllegalStateException {
        return getGuild().orElseThrow(() -> new IllegalStateException("No Guild set."));
    }

}
