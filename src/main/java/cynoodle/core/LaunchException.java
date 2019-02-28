/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import javax.annotation.Nonnull;

/**
 * Thrown by {@link CyNoodle#launch(LaunchSettings)} to indicate that launching failed.
 */
public final class LaunchException extends Exception {

    LaunchException(@Nonnull Throwable cause) {
        super(cause);
    }
}
