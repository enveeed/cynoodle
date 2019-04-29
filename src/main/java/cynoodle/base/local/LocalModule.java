/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.local;

import cynoodle.discord.UEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:local")
public final class LocalModule extends Module {
    private LocalModule() {}

    private static final EntityType<LocalPreferences> TYPE_PREFERENCES = EntityType.of(LocalPreferences.class);

    private UEntityManager<LocalPreferences> preferencesManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.preferencesManager = new UEntityManager<>(TYPE_PREFERENCES);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public UEntityManager<LocalPreferences> getPreferencesManager() {
        return this.preferencesManager;
    }
}
