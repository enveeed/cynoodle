/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Type registry for {@link EmbedType EmbedTypes}.
 */
public final class EmbedRegistry {

    /**
     * Stores the Embed types.
     */
    private final Map<String, EmbedType<?>> types = new HashMap<>();

    // ===

    EmbedRegistry() {}

    // ===

    @Nonnull
    public Optional<EmbedType<?>> find(@Nonnull String identifier) {
        return Optional.ofNullable(this.types.get(identifier));
    }

    // ===

    /**
     * Register an Embed type.
     * @param type the type to register
     */
    public void register(@Nonnull EmbedType<?> type) {

        String identifier = type.getIdentifier();

        // register
        this.types.put(identifier, type);
    }
}
