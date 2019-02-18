/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.input.Options;

import javax.annotation.Nonnull;

@CIdentifier("base:command:test")
@CAliases({"test","test-c","test-cmd","test-command"})
public final class TestCommand extends Command {
    private TestCommand() {}

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {
        context.getChannel()
                .sendMessage("received by: " + context.getMessage().getAuthor().getName()).queue();
    }
}
