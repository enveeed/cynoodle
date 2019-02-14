/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import com.mongodb.ConnectionString;
import cynoodle.core.api.Checks;

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
    private final ConnectionString DEF_MONGO_CONNECTION = new ConnectionString("mongodb://localhost");

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
}
