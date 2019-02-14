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
 * Entity which holds a Guild.
 * @see GEntity
 */
public interface GHolder {

    @Nonnull
    Optional<DiscordPointer> getGuild();

    void setGuild(@Nullable DiscordPointer guild);

}
