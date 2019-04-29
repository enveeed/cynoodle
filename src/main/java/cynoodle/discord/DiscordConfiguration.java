/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.discord;

import com.github.jsonj.JsonObject;
import cynoodle.Configuration;
import cynoodle.api.Checks;

import javax.annotation.Nonnull;

/**
 * Immutable configuration for Discord ({@link DiscordModule}).
 */
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

    public static class Builder {

        private String token;

        // ===

        private Builder() {}

        // ===

        @Nonnull
        public Builder setToken(@Nonnull String token) {
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

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    // ===

    @Nonnull
    public static DiscordConfiguration parse(@Nonnull Configuration.Section section) {

        JsonObject json = section.get();

        String token = json.maybeGetString("token")
                .orElseThrow(() -> new IllegalArgumentException("Discord configuration did not include token!"));

        //

        Builder builder = newBuilder();

        builder.setToken(token);

        return builder.build();
    }
}
