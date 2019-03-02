/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.text;

/**
 * An exception for parsers, that is thrown if an input
 * is being parsed but was not of the expected format.
 */
public class ParsingException extends IllegalArgumentException {

    public ParsingException() {}

    public ParsingException(String s) {
        super(s);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingException(Throwable cause) {
        super(cause);
    }
}
