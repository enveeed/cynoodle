/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core;

import cynoodle.core.api.text.Parameters;

import javax.annotation.Nonnull;

/**
 * Container for application launch parameters.
 */
public final class LaunchSettings {

    private final Parameters parameters;

    // ===

    private LaunchSettings(@Nonnull Parameters startParameters) {
        this.parameters = startParameters;
    }

    // ===

    @Nonnull
    public Parameters getParameters() {
        return this.parameters;
    }

    // ===

    @Nonnull
    public static LaunchSettings of(@Nonnull String[] input) {

        Parameters parameters = Parameters.of(input);

        return new LaunchSettings(parameters);
    }
}
