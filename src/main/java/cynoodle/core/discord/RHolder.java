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
 * An Entity which holds a Role.
 */
public interface RHolder {

    @Nonnull
    Optional<DiscordPointer> getRole();

    void setRole(@Nullable DiscordPointer role);

}
