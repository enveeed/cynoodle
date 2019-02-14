/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo;

/**
 * Exception for issues with the syntax, loading and saving of {@link BsonData}.
 */
public final class BsonDataException extends Exception {

    public BsonDataException() {}

    public BsonDataException(String message) {
        super(message);
    }

    public BsonDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public BsonDataException(Throwable cause) {
        super(cause);
    }
}
