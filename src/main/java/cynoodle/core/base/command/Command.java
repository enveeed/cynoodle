/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.input.Options;
import cynoodle.core.api.text.ParserException;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.EmbedBuilder;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Clock;

public abstract class Command {
    protected Command() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    private final CommandModule module = Module.get(CommandModule.class);

    // ===

    private CommandDescriptor descriptor;

    // ===

    // options available for all commands

    /**
     * A flag for the command to provide additional debug information
     */
    public static final Options.Option OPTION_DEBUG = Options.newFlagOption("debug", '#');

    /**
     * A flag for the command to localize the output format
     */
    public static final Options.Option OPTION_LOCALIZE = Options.newFlagOption("localize",'l');

    // ===

    protected final Options.Builder options = Options.newBuilder()
            .addOptions(OPTION_DEBUG)
            .addOptions(OPTION_LOCALIZE);

    // ===

    /**
     * Internal initialization method, also calls onInit() callback
     */
    final void init(@Nonnull CommandDescriptor descriptor) {
        this.descriptor = descriptor;

        this.onInit();
    }

    // === CALLBACKS ===

    /**
     * Called when this command class is initialized.
     */
    protected void onInit() {}

    // ===

    @Nonnull
    public CommandDescriptor getDescriptor() {
        return this.descriptor;
    }

    //

    /**
     * Get the identifier of this command.
     * @return the command identifier
     */
    @Nonnull
    public String getIdentifier() {
        return this.descriptor.getIdentifier();
    }

    // ===

    final void execute(@Nonnull CommandContext context) {

        // require the properties for this command on this guild
        CommandProperties properties = module.getProperties()
                .firstOrCreate(DiscordPointer.to(context.getGuild()), this.getIdentifier());

        // ===

        // TODO permissions

        long permission = properties.getPermission();

        // === OPTIONS ===

        Options options = this.options.build();

        Options.Result result;

        try {
            // parse the input
            result = options.parse(context.getRawInput());
        } catch (ParserException e) {

            // report command options parser input error

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFFCC4D));
            embed.setDescription("**⚠️ \u200B Failed to parse command parameters!**\n\n" + e.getMessage());

            context.getChannel().sendMessage(embed.build()).queue();

            return;
        }

        // === RUN ===

        long tStart = Clock.systemUTC().millis();

        try {
            // run the command
            this.run(context, result);
        } catch (Exception e) {
            handleException(context, result, e);
        }

        long tEnd = Clock.systemUTC().millis();

        if(result.hasOption(OPTION_DEBUG))
            context.getChannel().sendMessage("t: `" + (tEnd - tStart) + " ms`").queue();
    }

    // ===

    /**
     * Run the command.
     * This method may be called concurrently by multiple threads at the same time.
     * @param context the command context
     * @param input the command input
     * @throws Exception if the execution completed with an exception, will be handled
     * by {@link #handleException(CommandContext, Options.Result, Exception)}.
     */
    protected abstract void run(@Nonnull CommandContext context, @Nonnull Options.Result input) throws Exception;

    // ===

    /**
     * Handle an exception that was thrown by {@link #run(CommandContext, Options.Result)},
     * by outputting and reporting it.
     * @param context the command context
     * @param input the command input
     * @param exception the exception that was thrown
     */
    private void handleException(@Nonnull CommandContext context, @Nonnull Options.Result input, @Nonnull Exception exception) {

        if(exception instanceof CommandException) {

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFFCC4D));
            embed.setDescription("**⚠️ \u200B Command error!**\n\n" + exception.getMessage());

            context.getChannel().sendMessage(embed.build()).queue();

            return;
        }

        if(exception instanceof ParserException) {

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFFCC4D));
            embed.setDescription("**⚠️ \u200B Failed to parse input!**\n\n" + exception.getMessage());

            context.getChannel().sendMessage(embed.build()).queue();

            return;
        }

        // TODO other expected exception types

        // exception is nothing expected, must be reported and handled.

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0xDD2E44));
        embed.setDescription("**❌ \u200B Unexpected internal error!**\n\n" +
                "This was probably not your fault. Please try again later.");

        context.getChannel().sendMessage(embed.build()).queue();

        LOG.atWarning().withCause(exception)
                .log("Encountered unexpected internal error while executing " +
                        "command %s with parameters %s!", this.getIdentifier(), input);

    }

}