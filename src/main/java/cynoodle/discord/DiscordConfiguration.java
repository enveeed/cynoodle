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
