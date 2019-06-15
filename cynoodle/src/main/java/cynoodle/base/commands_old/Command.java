/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.commands;

import com.google.common.flogger.FluentLogger;
import cynoodle.module.Module;
import cynoodle.util.options.Option;
import cynoodle.util.options.Options;
import cynoodle.util.options.OptionsResult;
import cynoodle.util.parser.ParsingException;
import cynoodle.base.local.LocalContext;
import cynoodle.base.local.LocalModule;
import cynoodle.base.local.LocalPreferences;
import cynoodle.base.permissions.Permission;
import cynoodle.base.permissions.Permissions;
import cynoodle.CyNoodle;
import cynoodle.discord.DiscordModule;

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
    private static final Option OPT_DEBUG = Option.newFlagOption("debug", '#');

    /**
     * A flag for the command to localize the output format.
     */
    private static final Option OPT_LOCALIZE = Option.newFlagOption("localize",'l');

    /**
     * If the test instance is also present on a guild, this will force this instance to execute the command instead.
     */
    private static final Option OPT_IGNORE_TEST = Option.newFlagOption("ignore-test", null);

    // ===

    /**
     * Options builder for the command options.
     */
    private final Options.Builder optionsBuilder = Options.newBuilder()
            .add(OPT_DEBUG)
            .add(OPT_LOCALIZE)
            .add(OPT_IGNORE_TEST);

    // ===

    /**
     * Internal initialization method, also calls onInit() callback
     */
    final void init(@Nonnull CommandDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    // === CALLBACKS ===

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

    @Nonnull
    protected Options.Builder getOptionsBuilder() {
        return this.optionsBuilder;
    }

    // ===

    /**
     * Execute this command with the given context.
     * @param context the context.
     */
    final void execute(@Nonnull CommandContext context) {

        // === OPTIONS ===

        Options options = this.optionsBuilder.build();

        OptionsResult input;

        try {

            // parse the input
            input = options.parse(context.getRawInput());

        } catch (ParsingException e) {
            context.queueError(CommandErrors.commandParsingFailed(e));
            return;
        }

        // === TEST ACCOUNT ==

        // check if this is the test account
        boolean isTestAccount = Module.get(DiscordModule.class).isTestAccount();
        boolean ignoreTestAccount = input.hasOption(OPT_IGNORE_TEST);

        if(isTestAccount) {
            // do not execute the command because the normal instance will do it instead
            if(ignoreTestAccount) return;
        }

        // ===

        // require the settings for this guild
        CommandSettings.Properties properties = module.getSettings().firstOrCreate(context.getGuildPointer())
                .getProperties().findOrCreate(this.getIdentifier());

        // === PERMISSIONS ===

        boolean override = CyNoodle.get().getLaunchSettings().isNoPermissionsEnabled();

        Permissions permissions = Permissions.get();
        Permission permission = Permission.of(descriptor.getPermission());

        if(!permissions.test(context.getMember(), permission) && !override) {
            context.queueError(CommandErrors.permissionInsufficient());
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
                ce = CommandErrors.internalError();
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