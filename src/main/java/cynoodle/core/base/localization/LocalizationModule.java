/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.localization;

import cynoodle.core.discord.UEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:localization</code>
 */
@MIdentifier("base:localization")
public final class LocalizationModule extends Module {
    private LocalizationModule() {}

    private static final EntityType<Localization> TYPE_LOCALIZATION = EntityType.of(Localization.class);

    private UEntityManager<Localization> localizationManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.localizationManager = new UEntityManager<>(TYPE_LOCALIZATION);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public UEntityManager<Localization> getLocalizationManager() {
        return this.localizationManager;
    }
}
