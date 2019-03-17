/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import cynoodle.core.api.text.IntegerParser;
import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Container for application launch parameters.
 */
public final class LaunchSettings {

    /**
     * Raw input parameters.
     */
    private final Parameters parameters;

    //

    /**
     * The logging level.
     */
    private final int logLevel;

    private boolean noPermissions;

    private boolean setupTest;

    // ===

    private LaunchSettings(@Nonnull Parameters startParameters, @Nonnull Collector collector) {
        this.parameters = startParameters;

        this.logLevel = collector.logLevel;
        this.noPermissions = collector.noPermissions;
        this.setupTest = collector.setupTest;
    }

    // ===

    @Nonnull
    public Parameters getParameters() {
        return this.parameters;
    }

    //

    public int getLogLevel() {
        return this.logLevel;
    }

    public boolean isNoPermissionsEnabled() {
        return noPermissions;
    }

    public boolean isSetupTestEnabled() {
        return this.setupTest;
    }

    // ===

    private static final class Collector {
        private Collector() {}

        // ===

        int logLevel = Level.INFO.intValue();
        boolean noPermissions = false;
        boolean setupTest = false;

    }

    // ===

    private static final Options.Option OPT_LOG_LEVEL
            = Options.newValueOption("log",'l');

    private static final Options.Option OPT_NO_PERMISSIONS
            = Options.newFlagOption("no-permissions",null);

    private static final Options.Option OPT_SETUP_TEST
            = Options.newFlagOption("setup-test",null);

    //

    @Nonnull
    public static LaunchSettings of(@Nonnull String[] input) {

        Parameters parameters = Parameters.of(input);
        Collector collector = new Collector();

        //

        Options options = Options.newBuilder()
                .addOptions(OPT_LOG_LEVEL, OPT_NO_PERMISSIONS, OPT_SETUP_TEST)
                .build();

        //

        Options.Result result = options.parse(parameters);

        if(result.hasOption(OPT_LOG_LEVEL)) {
            collector.logLevel = IntegerParser.get().parse(result.getOptionValue(OPT_LOG_LEVEL));
        }

        collector.noPermissions = result.hasOption(OPT_NO_PERMISSIONS);
        collector.setupTest = result.hasOption(OPT_SETUP_TEST);

        //

        return new LaunchSettings(parameters, collector);
    }
}
