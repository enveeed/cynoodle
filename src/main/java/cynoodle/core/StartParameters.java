/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import com.mongodb.ConnectionString;
import cynoodle.core.api.Checks;
import cynoodle.core.api.input.Options;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Start parameters for cynoodle, including defaults.
 */
public final class StartParameters {

    private final static Path DEF_CONFIGURATION_FILE = Paths.get("config.json");

    // TODO temporary:
    private final static ConnectionString DEF_MONGO_CONNECTION = new ConnectionString("mongodb://localhost");

    // ======

    private final static Options.Option OPT_TOKEN = Options.newValueOption("token", 't');
    private final static Options.Option OPT_MONGO_CONNECTION = Options.newValueOption("db", 'd');

    private final static Options OPTIONS = Options.newBuilder()
            .addOptions(
                    OPT_TOKEN,
                    OPT_MONGO_CONNECTION)
            .build();

    // ======

    private final Path configurationFile;

    // TODO temporary:
    private final String discordToken;
    private final ConnectionString mongoConnection;

    // ===

    private StartParameters(@Nonnull Builder builder) {
        this.configurationFile = builder.configurationFile != null ? builder.configurationFile : DEF_CONFIGURATION_FILE;

        // TODO temporary:
        this.discordToken = builder.discordToken;
        this.mongoConnection = builder.mongoConnection != null ? builder.mongoConnection : DEF_MONGO_CONNECTION;
    }

    // ===

    /**
     * Get the path to the general configuration file.
     * @return the configuration file path.
     */
    @Nonnull
    public Path getConfigurationFile() {
        return this.configurationFile;
    }

    // TODO temporary:

    @Nonnull
    public String getDiscordToken() {
        return this.discordToken;
    }

    @Nonnull
    public ConnectionString getMongoConnection() {
        return this.mongoConnection;
    }


    // ===

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    // ===

    public static class Builder {

        private Path configurationFile;

        // TODO temporary:
        private String discordToken;
        private ConnectionString mongoConnection;

        // ===

        private Builder() {}

        // ===

        @Nonnull
        public Builder setConfigurationFile(@Nullable Path configurationFile) {
            this.configurationFile = configurationFile;
            return this;
        }

        // TODO temporary:
        @Nonnull
        public Builder setDiscordToken(@Nonnull String discordToken) {
            this.discordToken = discordToken;
            return this;
        }

        @Nonnull
        public Builder setMongoConnection(@Nullable ConnectionString mongoConnection) {
            this.mongoConnection = mongoConnection;
            return this;
        }


        // ===

        @Nonnull
        public StartParameters build() {
            // TODO temporary:
            Checks.notNull(this.discordToken, "discordToken");

            return new StartParameters(this);
        }
    }

    // ===

    @Nonnull
    public static StartParameters parse(@Nonnull String input) throws ParserException {

        Options.Result result = OPTIONS.parse(input);

        Builder builder = newBuilder();

        //

        if(result.hasOption(OPT_TOKEN))
            builder.setDiscordToken(result.getOptionValue(OPT_TOKEN));
        if(result.hasOption(OPT_MONGO_CONNECTION))
            builder.setMongoConnection(new ConnectionString(result.getOptionValue(OPT_MONGO_CONNECTION)));

        //

        return builder.build();
    }
}
