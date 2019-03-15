/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

/**
 * Indicates a problem with a {@link Command} class.
 */
public final class CommandClassException extends RuntimeException {

    CommandClassException() {}

    CommandClassException(String message) {
        super(message);
    }

    CommandClassException(String message, Throwable cause) {
        super(message, cause);
    }

    CommandClassException(Throwable cause) {
        super(cause);
    }
}
