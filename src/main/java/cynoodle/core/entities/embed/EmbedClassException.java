/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

/**
 * Indicates a problem with an {@link Embed} class.
 */
public final class EmbedClassException extends RuntimeException {

    EmbedClassException() {}

    EmbedClassException(String message) {
        super(message);
    }

    EmbedClassException(String message, Throwable cause) {
        super(message, cause);
    }

    EmbedClassException(Throwable cause) {
        super(cause);
    }
}
