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

package cynoodle.base.fm;

import com.github.jsonj.JsonObject;
import cynoodle.Configuration;
import cynoodle.util.Checks;

import javax.annotation.Nonnull;

/**
 * Immutable configuration for {@link FMModule}.
 */
public final class FMModuleConfiguration {

    private final String apiKey;

    // ===

    private FMModuleConfiguration(@Nonnull Builder builder) {
        this.apiKey = builder.apiKey;
    }

    // ===

    @Nonnull
    public String getAPIKey() {
        return apiKey;
    }

    // ===

    public static class Builder {
        private Builder() {}

        private String apiKey;

        // ===

        @Nonnull
        public Builder setAPIKey(@Nonnull String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        // ===

        @Nonnull
        public FMModuleConfiguration build() {
            Checks.notNull(apiKey, "apiKey");
            return new FMModuleConfiguration(this);
        }
    }

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    // ===

    @Nonnull
    public static FMModuleConfiguration parse(@Nonnull Configuration.Section section) throws IllegalArgumentException {

        JsonObject json = section.get();

        String apiKey = json.maybeGetString("apiKey")
                .orElseThrow(() -> new IllegalArgumentException("FMModule configuration is missing api key!"));

        //

        Builder builder = newBuilder();

        builder.setAPIKey(apiKey);

        return builder.build();
    }
}
