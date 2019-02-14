/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import javax.annotation.Nonnull;

final class StopException extends Exception {

    StopException(@Nonnull Throwable cause) {
        super(cause);
    }
}
