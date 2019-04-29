/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle;

import com.github.jsonj.JsonElement;
import com.github.jsonj.JsonObject;
import com.github.jsonj.tools.JsonParser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * General cynoodle configuration.
 * NOTE: This really only wraps the JSON Api to avoid having to deal with it manually,
 * also in case the JSON Api gets replaced by a different one at some point.
 */
public final class Configuration {

    private final JsonObject content;

    // ===

    private Configuration(@Nonnull JsonObject content) {
        this.content = content;
    }

    // ===

    /**
     * Get the Section in the configuration for the given name.
     * @param section the section key
     * @return the Section at this key, otherwise empty.
     */
    @Nonnull
    public Optional<Section> get(@Nonnull String section) {
        return this.content.maybeGetObject(section).map(Section::new);
    }

    // ===

    /**
     * A "section" in the configuration file.
     * That is a key-identified top level object.
     */
    public final class Section {

        private final JsonObject content;

        private Section(@Nonnull JsonObject content) {
            this.content = content;
        }

        // ===

        @Nonnull
        public JsonObject get() {
            return this.content;
        }
    }

    // ===

    @Nonnull
    public static Configuration read(@Nonnull Path configurationFile) throws IOException  {

        byte[] bytes = Files.readAllBytes(configurationFile);

        String content = new String(bytes, StandardCharsets.UTF_8);

        JsonParser parser = new JsonParser();

        JsonElement json = parser.parse(content);

        if(!json.isObject()) throw new IllegalArgumentException("Configuration must be JSON Object.");

        return new Configuration(json.asObject());
    }
}
