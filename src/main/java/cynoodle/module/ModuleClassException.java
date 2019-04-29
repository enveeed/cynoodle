/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.module;

/**
 * Indicates a problem with a {@link Module} class.
 */
public class ModuleClassException extends Exception {

    ModuleClassException() {}

    ModuleClassException(String message) {
        super(message);
    }

    ModuleClassException(String message, Throwable cause) {
        super(message, cause);
    }

    ModuleClassException(Throwable cause) {
        super(cause);
    }
}
