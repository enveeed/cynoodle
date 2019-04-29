/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.module;

public class ModuleStartException extends Exception {

    ModuleStartException() {}

    ModuleStartException(String message) {
        super(message);
    }

    ModuleStartException(String message, Throwable cause) {
        super(message, cause);
    }

    ModuleStartException(Throwable cause) {
        super(cause);
    }
}
