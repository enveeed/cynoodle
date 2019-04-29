/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.fm;

import com.github.jsonj.JsonObject;
import cynoodle.Configuration;
import cynoodle.api.Checks;

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
