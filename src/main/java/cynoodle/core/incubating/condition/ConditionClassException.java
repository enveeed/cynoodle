/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;

public final class ConditionClassException extends RuntimeException {

    ConditionClassException() {}

    ConditionClassException(String message) {
        super(message);
    }

    ConditionClassException(String message, Throwable cause) {
        super(message, cause);
    }

    ConditionClassException(Throwable cause) {
        super(cause);
    }
}
