/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.input.Options;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;

@CIdentifier("base:command:test")
@CAliases({"test"})
public final class TestCommand extends Command {
    private TestCommand() {}

    private static final Options.Option OPT_SEND = Options.newFlagOption("send", '1');
    private static final Options.Option OPT_THROW_PEX = Options.newFlagOption("throw-pex",'2');
    private static final Options.Option OPT_THROW_EX = Options.newFlagOption("throw-ex", '3');
    private static final Options.Option OPT_RESPOND = Options.newValueOption("respond", '4');

    @Override
    protected void onInit() {
        this.options.addOptions(
                OPT_SEND,
                OPT_THROW_PEX,
                OPT_THROW_EX,
                OPT_RESPOND
        );
    }

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception {

        System.out.println("TestCommand executed: " + context.getRawInput());

        if(input.hasOption(OPT_SEND))
            context.getChannel().sendMessage("received by: " + context.getMessage().getAuthor().getName()).queue();
        else if(input.hasOption(OPT_THROW_PEX))
            throw new ParserException("This is a test ParserException.");
        else if(input.hasOption(OPT_THROW_EX))
            throw new Exception("This is a unexpected Exception.");
        else if(input.hasOption(OPT_RESPOND)) {
            context.getChannel().sendMessage("Responding with: " + input.getOptionValue(OPT_RESPOND)).queue();
        }
    }
}
