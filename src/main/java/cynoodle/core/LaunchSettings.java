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

    // === RAW ===

    /**
     * Raw input parameters.
     */
    private final Parameters parameters;

    // === LOGGING ===

    /**
     * The logging level.
     */
    private final int logLevel;


    // === PERMISSIONS ===

    private boolean noPermissions;

    // === DEBUGGING ===

    private boolean setupTest;

    // ===

    private boolean convertLegacy;

    // ===

    private LaunchSettings(@Nonnull Parameters startParameters, @Nonnull Collector collector) {
        this.parameters = startParameters;

        this.logLevel = collector.logLevel;
        this.noPermissions = collector.noPermissions;
        this.setupTest = collector.setupTest;
        this.convertLegacy = collector.convertLegacy;
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

    public boolean isConvertLegacyEnabled() {
        return this.convertLegacy;
    }

    // ===

    private static final class Collector {
        private Collector() {}

        // ===

        int logLevel = Level.INFO.intValue();
        boolean noPermissions = false;
        boolean setupTest = false;
        boolean convertLegacy = false;

    }

    // ===

    private static final Options.Option OPT_LOG_LEVEL
            = Options.newValueOption("log",'l');

    private static final Options.Option OPT_NO_PERMISSIONS
            = Options.newFlagOption("no-permissions",null);

    private static final Options.Option OPT_SETUP_TEST
            = Options.newFlagOption("setup-test",null);

    private static final Options.Option OPT_CONVERT_LEGACY
            = Options.newFlagOption("convert-legacy",null);

    //

    @Nonnull
    public static LaunchSettings of(@Nonnull String[] input) {

        Parameters parameters = Parameters.of(input);
        Collector collector = new Collector();

        //

        Options options = Options.newBuilder()
                .add(OPT_LOG_LEVEL, OPT_NO_PERMISSIONS, OPT_SETUP_TEST, OPT_CONVERT_LEGACY)
                .build();

        //

        Options.Result result = options.parse(parameters);

        if(result.hasOption(OPT_LOG_LEVEL)) {
            collector.logLevel = IntegerParser.get().parse(result.getOptionValue(OPT_LOG_LEVEL));
        }

        collector.noPermissions = result.hasOption(OPT_NO_PERMISSIONS);
        collector.setupTest = result.hasOption(OPT_SETUP_TEST);
        collector.convertLegacy = result.hasOption(OPT_CONVERT_LEGACY);

        //

        return new LaunchSettings(parameters, collector);
    }
}
