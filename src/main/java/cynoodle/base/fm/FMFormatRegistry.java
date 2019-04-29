/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.fm;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class FMFormatRegistry {
    FMFormatRegistry() {}

    // ===

    private final Map<String, FMFormat> formats = new HashMap<>();

    // ===

    @Nonnull
    public Optional<FMFormat> find(@Nonnull String key) {
        return Optional.ofNullable(this.formats.get(key));
    }

    // ===

    public void register(@Nonnull String key, @Nonnull FMFormat format) {
        this.formats.put(key, format);
    }
}
