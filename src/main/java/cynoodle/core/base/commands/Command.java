/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.CyNoodle;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.ParsingException;
import cynoodle.core.base.access.AccessModule;
import cynoodle.core.base.access.AccessControl;
import cynoodle.core.base.local.LocalPreferences;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.base.local.LocalModule;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Clock;

public abstract class Command {
    protected Command() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    private final CommandsModule module = Module.get(CommandsModule.class);

    // ===

    private CommandDescriptor descriptor;

    // === DEFAULT OPTIONS ==

    /**
     * A flag for the command to provide additional debug information.
     */
    private static final Options.Option OPT_DEBUG = Options.newFlagOption("debug", '#');

    /**
     * A flag for the command to localize the output format.
     */
    private static final Options.Option OPT_LOCALIZE = Options.newFlagOption("localize",'l');

    // ===

    /**
     * Options builder for the command options.
     */
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

        // require the settings for this guild
        CommandSettings.Properties properties = module.getSettings().firstOrCreate(context.getGuildPointer())
                .getProperties().findOrCreate(this.getIdentifier());

        // === PERMISSIONS ===

        boolean override = CyNoodle.get().getLaunchSettings().isNoPermissionsEnabled();

        //

        AccessControl ac = Module.get(AccessModule.class).getSettingsManager()
                .firstOrCreate(context.getGuildPointer());

        String permission = descriptor.getPermission();

        boolean passedPermissions = ac.test(context.getUser(), permission) || override;

        if(!passedPermissions) {
            context.queueError(CommandErrors.permissionInsufficient(this));
            return;
        }

        // === OPTIONS ===

        Options options = this.options.build();

        Options.Result input;

        try {

            // parse the input
            input = options.parse(context.getRawInput());

        } catch (ParsingException e) {
            context.queueError(CommandErrors.commandParsingFailed(this, e));
            return;
        }

        // === LOCALIZATION ===

        LocalContext local;

        if(input.hasOption(OPT_LOCALIZE)) {

            // acquire the localization for the user
            LocalModule module = Module.get(LocalModule.class);

            LocalPreferences localPreferences = module.getPreferencesManager()
                    .firstOrCreate(context.getUserPointer());

            local = LocalContext.of(localPreferences);
        }
        else local = LocalContext.ofDefault();

        // === RUN ===

        long tStart = Clock.systemUTC().millis();

        try {
            // run the command
            this.run(context, CommandInput.wrap(this, input), local);
        } catch (Exception e) {

            CommandError ce;

            if(e instanceof CommandError) ce = (CommandError) e;
            else {
                ce = CommandErrors.internalError(this);
                LOG.atSevere().withCause(e).log("Internal error at %s with input %s!", this.getIdentifier(), input);
            }

            context.queueError(ce);
        }

        long tEnd = Clock.systemUTC().millis();

        if(input.hasOption(OPT_DEBUG))
            context.getChannel().sendMessage("t: `" + (tEnd - tStart) + " ms`").queue();
    }

    // ===

    /**
     * Run the command.
     * This method may be called concurrently by multiple threads at the same time.
     * @param context the command context
     * @param input the command input
     * @param local the localization context
     * @throws Exception if the execution completed with a exception, will be handled
     */
    protected abstract void run(@Nonnull CommandContext context,
                                @Nonnull CommandInput input,
                                @Nonnull LocalContext local)
            throws Exception;

}