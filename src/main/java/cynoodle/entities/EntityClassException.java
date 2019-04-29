/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.entities;

/**
 * Indicates a problem with an {@link Entity} class.
 */
public final class EntityClassException extends RuntimeException {

    EntityClassException() {}

    EntityClassException(String message) {
        super(message);
    }

    EntityClassException(String message, Throwable cause) {
        super(message, cause);
    }

    EntityClassException(Throwable cause) {
        super(cause);
    }
}
