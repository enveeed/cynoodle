/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import javax.annotation.Nonnull;

/**
 * A special exception for command execution,
 * which can carry a {@link CommandError} to describe the error.
 * The 'message' contents of the error will be used as the message of the exception.
 */
public final class CommandException extends Exception {

    private final CommandError error;

    // ===

    public CommandException(@Nonnull CommandError error) {
        super(error.getMessage());
        this.error = error;
    }

    public CommandException(@Nonnull CommandError error, @Nonnull Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    // ===

    @Nonnull
    public CommandError getError() {
        return this.error;
    }
}
