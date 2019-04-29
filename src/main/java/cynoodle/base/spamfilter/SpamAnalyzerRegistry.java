/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.spamfilter;

import javax.annotation.Nonnull;
import java.util.*;

public final class SpamAnalyzerRegistry {
    SpamAnalyzerRegistry() {}

    // ===

    private final Map<String, SpamAnalyzer> analyzers = new HashMap<>();

    // ===

    @Nonnull
    public Optional<SpamAnalyzer> find(@Nonnull String identifier) {
        return Optional.ofNullable(this.analyzers.get(identifier));
    }

    @Nonnull
    public Set<Map.Entry<String, SpamAnalyzer>> all() {
        return this.analyzers.entrySet();
    }

    // ===

    public void register(@Nonnull String identifier, @Nonnull SpamAnalyzer analyzer) {
        this.analyzers.put(identifier, analyzer);
    }

}
