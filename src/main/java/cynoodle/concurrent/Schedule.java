/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.concurrent;

import javax.annotation.Nonnull;
import java.time.Instant;

public interface Schedule {

    @Nonnull
    Instant next();

}
