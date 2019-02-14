/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.Checks;

import javax.annotation.Nonnull;

/**
 * Immutable configuration for Discord ({@link DiscordModule}).
 */
// TODO parse from JSON
public final class DiscordConfiguration {

    private final String token;

    // ===

    private DiscordConfiguration(@Nonnull Builder builder) {
        this.token = builder.token;
    }

    // ===

    @Nonnull
    public String getToken() {
        return token;
    }

    // ===

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    // ===

    public static class Builder {

        private String token;

        // ===

        private Builder() {}

        // ===

        @Nonnull
        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        //

        @Nonnull
        public DiscordConfiguration build() {

            Checks.notNull(token, "token");

            return new DiscordConfiguration(this);
        }
    }
}
