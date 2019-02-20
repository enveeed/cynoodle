/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

/**
 * Indicates a problem with a {@link CommandContext}.
 */
public final class CommandContextException extends Exception {

    CommandContextException() {
    }

    CommandContextException(String message) {
        super(message);
    }

    CommandContextException(String message, Throwable cause) {
        super(message, cause);
    }

    CommandContextException(Throwable cause) {
        super(cause);
    }
}
