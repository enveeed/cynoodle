/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.module;

public class ModuleStopException extends Exception {

    ModuleStopException() {}

    ModuleStopException(String message) {
        super(message);
    }

    ModuleStopException(String message, Throwable cause) {
        super(message, cause);
    }

    ModuleStopException(Throwable cause) {
        super(cause);
    }
}
