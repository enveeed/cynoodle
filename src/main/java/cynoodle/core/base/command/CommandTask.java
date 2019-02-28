/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.text.Options;

import javax.annotation.Nonnull;

/**
 * A task for a single execution of a {@link Command}.
 * @see Command
 */
public interface CommandTask {

    // TODO add parsed options ?
    void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception;
}
