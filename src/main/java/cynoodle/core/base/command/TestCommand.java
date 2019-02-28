/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.ParserException;
import cynoodle.core.base.xp.Rank;
import cynoodle.core.base.xp.RankManager;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@CIdentifier("base:command:test")
@CAliases({"test"})
public final class TestCommand extends Command {
    private TestCommand() {}

    private static final Options.Option OPT_SEND = Options.newFlagOption("send", '1');
    private static final Options.Option OPT_THROW_PEX = Options.newFlagOption("throw-pex",'2');
    private static final Options.Option OPT_THROW_EX = Options.newFlagOption("throw-ex", '3');
    private static final Options.Option OPT_RESPOND = Options.newValueOption("respond", '4');
    private static final Options.Option OPT_SETUP = Options.newFlagOption("setup", 's');

    @Override
    protected void onInit() {
        this.options.addOptions(
                OPT_SEND,
                OPT_THROW_PEX,
                OPT_THROW_EX,
                OPT_RESPOND,
                OPT_SETUP
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
        } else if(input.hasOption(OPT_SETUP)) {

            context.getChannel().sendMessage("Setting up test data ...").queue();

            XPModule xpModule = Module.get(XPModule.class);

            RankManager ranks = xpModule.getRankManager();

            Rank r3 = ranks.create(context.getGuildPointer(), "nice blue girl", 3);
            Rank r6 = ranks.create(context.getGuildPointer(), "pink dude", 6);
            Rank r10 = ranks.create(context.getGuildPointer(), "orange bro", 10);

            Rank.Role metaRole = Rank.createRole(DiscordPointer.to(549201532931932206L));
            metaRole.setKeepEnabled(true);

            r3.addRoles(Rank.createRole(DiscordPointer.to(410133534175526912L)));
            r6.addRoles(Rank.createRole(DiscordPointer.to(410132981945204756L)), metaRole);
            r10.addRoles(Rank.createRole(DiscordPointer.to(411610242619670529L)));

            ranks.persistAll(r3, r6, r10);

            context.getChannel().sendMessage("Test data was created!").queue();

        }
    }
}
