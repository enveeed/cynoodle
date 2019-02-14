/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

/**
 * Indicates a problem with the data IO of an {@link Entity} instance.
 */
public final class EntityIOException extends RuntimeException {

    EntityIOException() {
    }

    EntityIOException(String message) {
        super(message);
    }

    EntityIOException(String message, Throwable cause) {
        super(message, cause);
    }

    EntityIOException(Throwable cause) {
        super(cause);
    }
}
