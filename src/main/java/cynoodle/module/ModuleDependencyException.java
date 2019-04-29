/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.module;

public class ModuleDependencyException extends Exception {

    ModuleDependencyException() {
    }

    ModuleDependencyException(String message) {
        super(message);
    }

    ModuleDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    ModuleDependencyException(Throwable cause) {
        super(cause);
    }
}
