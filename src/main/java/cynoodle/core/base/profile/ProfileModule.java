/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profile;

import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

@MIdentifier("base:profile")
@MRequires("base:command")
public final class ProfileModule extends Module {
    private ProfileModule() {}

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
