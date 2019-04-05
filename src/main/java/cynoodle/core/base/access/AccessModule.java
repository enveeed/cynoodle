/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

/**
 * Access Control.
 */
@MIdentifier("base:access")
public final class AccessModule extends Module {
    private AccessModule() {}

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
