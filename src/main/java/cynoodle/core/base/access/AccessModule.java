/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * Access Control.
 */
@MIdentifier("base:access")
public final class AccessModule extends Module {
    private AccessModule() {}

    static final EntityType<AccessControl> TYPE_SETTINGS = EntityType.of(AccessControl.class);

    // ===

    private GEntityManager<AccessControl> settingsManager;

    // ===

    @Override
    protected void start() {
        super.start();

        //

        settingsManager = new GEntityManager<>(TYPE_SETTINGS);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public GEntityManager<AccessControl> getSettingsManager() {
        return this.settingsManager;
    }
}
