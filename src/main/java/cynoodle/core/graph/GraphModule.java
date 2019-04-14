/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.graph;

import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MSystem;
import cynoodle.core.module.Module;
import graphql.GraphQL;

/**
 * Provides the {@link graphql.GraphQL GraphQL} API.
 */
@MIdentifier("graph")
@MSystem
public final class GraphModule extends Module {
    private GraphModule() {}

    // ===

    /**
     * The GraphQL instance.
     */
    private GraphQL graph = null;

    // ===

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
