/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.Parameters;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;

/**
 * Parameters for system:discord.
 */
public final class DiscordParameters {

    private final static Options.Option OPT_TOKEN = Options.newValueOption("token", 't');

    // ===

    /**
     * The Discord API token.
     */
    private final String token;

    // ===

    private DiscordParameters(@Nonnull String token) {
        this.token = token;
    }

    // ===

    @Nonnull
    public String getToken() {
        return this.token;
    }

    // ===

    @Nonnull
    public static DiscordParameters parse(@Nonnull Parameters input) throws ParserException {

        Options options = Options.newBuilder()
                .addOptions(OPT_TOKEN)
                .setIgnoreUnknownOptions(true)
                .build();

        //

        Options.Result result = options.parse(input);

        //

        String token = result.getOptionValue(OPT_TOKEN);

        //

        return new DiscordParameters(token);
    }
}
