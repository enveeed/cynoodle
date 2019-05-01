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

package cynoodle;

import cynoodle.api.text.Options;
import cynoodle.api.text.Parameters;
import cynoodle.api.parser.PrimitiveParsers;

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

    private String configPath;

    // ===

    private LaunchSettings(@Nonnull Parameters startParameters, @Nonnull Collector collector) {
        this.parameters = startParameters;

        this.logLevel = collector.logLevel;
        this.noPermissions = collector.noPermissions;
        this.setupTest = collector.setupTest;
        this.configPath = collector.configPath;
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

    public String getConfigPath() {
        return this.configPath;
    }

    // ===

    private static final class Collector {
        private Collector() {}

        // ===

        int logLevel = Level.INFO.intValue();
        boolean noPermissions = false;
        boolean setupTest = false;
        String configPath = "./config.json";

    }

    // ===

    private static final Options.Option OPT_LOG_LEVEL
            = Options.newValueOption("log",'l');

    private static final Options.Option OPT_NO_PERMISSIONS
            = Options.newFlagOption("no-permissions",null);

    private static final Options.Option OPT_SETUP_TEST
            = Options.newFlagOption("setup-test",null);

    private static final Options.Option OPT_CONFIG
            = Options.newFlagOption("config",'c');

    //

    @Nonnull
    public static LaunchSettings of(@Nonnull String[] input) {

        Parameters parameters = Parameters.of(input);
        Collector collector = new Collector();

        //

        Options options = Options.newBuilder()
                .add(OPT_LOG_LEVEL, OPT_NO_PERMISSIONS, OPT_SETUP_TEST, OPT_CONFIG)
                .build();

        //

        Options.Result result = options.parse(parameters);

        if(result.hasOption(OPT_LOG_LEVEL)) {
            collector.logLevel = PrimitiveParsers.parseInteger().parse(result.getOptionValue(OPT_LOG_LEVEL));
        }

        collector.noPermissions = result.hasOption(OPT_NO_PERMISSIONS);
        collector.setupTest = result.hasOption(OPT_SETUP_TEST);

        if(result.hasOption(OPT_CONFIG)) {
            collector.configPath = result.getOptionValue(OPT_CONFIG);
        }

        //

        return new LaunchSettings(parameters, collector);
    }
}
