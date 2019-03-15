/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.Configuration;
import cynoodle.core.CyNoodle;
import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:fm</code>
 */
@MIdentifier("base:fm")
@MRequires("base:commands")
public final class FMModule extends Module {
    private FMModule() {}

    // ===

    private static final EntityType<FMPreferences> TYPE_PREFERENCES = EntityType.of(FMPreferences.class);

    // ===

    private FMModuleConfiguration configuration;

    //

    private UEntityManager<FMPreferences> preferencesManager;

    // ===

    @Override
    protected void start() {
        super.start();

        Configuration.Section section = CyNoodle.get().getConfiguration().get("fm")
                .orElseThrow(() -> new RuntimeException("last.fm configuration is missing! (section 'fm')"));

        this.configuration = FMModuleConfiguration.parse(section);

        //

        preferencesManager = new UEntityManager<>(TYPE_PREFERENCES);

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(FMCommand.class);
        registry.register(FMEditCommand.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public FMModuleConfiguration getConfiguration() {
        return this.configuration;
    }

    //

    @Nonnull
    public UEntityManager<FMPreferences> getPreferencesManager() {
        return this.preferencesManager;
    }
}
