/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle;

import javax.annotation.Nonnull;

final class StartException extends Exception {

    StartException(@Nonnull Throwable cause) {
        super(cause);
    }

}
