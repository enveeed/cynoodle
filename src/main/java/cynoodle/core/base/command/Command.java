/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.ParsingException;
import cynoodle.core.base.localization.Localization;
import cynoodle.core.base.localization.LocalizationContext;
import cynoodle.core.base.localization.LocalizationModule;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.EmbedBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private static final Options.Option OPT_DEBUG = Options.newFlagOption("debug", '#');

    /**
     * A flag for the command to localize the output format
     */
    private static final Options.Option OPT_LOCALIZE = Options.newFlagOption("localize",'l');

    // ===

    protected final Options.Builder options = Options.newBuilder()
            .addOptions(OPT_DEBUG)
            .addOptions(OPT_LOCALIZE);

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

    /**
     * Execute this command with the given context.
     * @param context the context.
     */
    final void execute(@Nonnull CommandContext context) {

        // require the properties for this command on this guild
        CommandProperties properties = module.getProperties()
                .firstOrCreate(DiscordPointer.to(context.getGuild()), this.getIdentifier());

        // === PERMISSIONS ===

        // TODO permissions

        long permission = properties.getPermission();

        // === OPTIONS ===

        Options options = this.options.build();

        Options.Result input;

        try {

            // parse the input
            input = options.parse(context.getRawInput());

        } catch (ParsingException e) {

            // report command options parser input error
            handleException(context, null, e);

            return;
        }

        // === LOCALIZATION ===

        LocalizationContext local;

        if(input.hasOption(OPT_LOCALIZE)) {

            // acquire the localization for the user
            LocalizationModule module = Module.get(LocalizationModule.class);

            Localization localization = module.getLocalizationManager()
                    .firstOrCreate(context.getUserPointer());

            local = LocalizationContext.of(localization);
        }
        else local = LocalizationContext.ofDefault();

        // === RUN ===

        long tStart = Clock.systemUTC().millis();

        try {
            // run the command
            this.run(context, local, input);
        } catch (Exception e) {
            handleException(context, input, e);
        }

        long tEnd = Clock.systemUTC().millis();

        if(input.hasOption(OPT_DEBUG))
            context.getChannel().sendMessage("t: `" + (tEnd - tStart) + " ms`").queue();
    }

    //

    /**
     * Run the command.
     * This method may be called concurrently by multiple threads at the same time.
     * @param context the command context
     * @param local the localization context
     * @param input the command input
     * @throws Exception if the execution completed with an exception, will be handled
     */
    protected abstract void run(@Nonnull CommandContext context, @Nonnull LocalizationContext local, @Nonnull Options.Result input)
            throws Exception;

    // ===

    private void handleException(@Nonnull CommandContext context, @Nullable Options.Result input, @Nonnull Exception thrown) {

        CommandException exception;

        //

        if(thrown instanceof CommandException) {

            // directly thrown, handle it directly
            exception = (CommandException) thrown;

        } else if(thrown instanceof ParsingException) {

            // parsing error
            exception = CommandExceptions.parsingFailed((ParsingException) thrown);

        } else {

            // create a new exception for internal error
            // and report the unexpected error

            exception = CommandExceptions.internalError();

            LOG.atSevere().withCause(thrown)
                    .log("Unexpected internal error while executing %s (input: %s)", this.getIdentifier(), input);
        }

        //

        EmbedBuilder embed = new EmbedBuilder();

        String title    = exception.getTitle();
        String message  = exception.getMessage();
        String icon     = exception.getIcon();
        Color color     = exception.getColor();

        StringBuilder content = new StringBuilder();

        if(icon != null) content.append(icon).append(" \u200b ");
        if(title != null) content.append("**").append(title).append("**\n\n");
        if(message != null) content.append(message).append("\n\n");

        if(exception.hasFlag(CommandException.Flag.DISPLAY_USAGE)) {
            // TODO append usage
        }

        // TODO more flags

        if(content.length() == 0) content.append("Unknown error.");

        embed.setDescription(content.toString());

        if(color != null) embed.setColor(color);

        //

        context.getChannel().sendMessage(embed.build()).queue();

    }

}