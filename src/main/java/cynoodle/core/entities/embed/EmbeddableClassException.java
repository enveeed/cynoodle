/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

/**
 * Indicates a problem with an {@link Embeddable} class.
 */
public final class EmbeddableClassException extends RuntimeException {

    EmbeddableClassException() {}

    EmbeddableClassException(String message) {
        super(message);
    }

    EmbeddableClassException(String message, Throwable cause) {
        super(message, cause);
    }

    EmbeddableClassException(Throwable cause) {
        super(cause);
    }
}
