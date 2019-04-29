/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.fm;

import cynoodle.Configuration;
import cynoodle.CyNoodle;
import cynoodle.base.commands.CommandsModule;
import cynoodle.base.commands.CommandRegistry;
import cynoodle.discord.UEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

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

    private FMFormatRegistry formatRegistry;

    // ===

    @Override
    protected void start() {
        super.start();

        Configuration.Section section = CyNoodle.get().getConfiguration().get("fm")
                .orElseThrow(() -> new RuntimeException("last.fm configuration is missing! (section 'fm')"));

        this.configuration = FMModuleConfiguration.parse(section);

        //

        this.preferencesManager = new UEntityManager<>(TYPE_PREFERENCES);

        this.formatRegistry = new FMFormatRegistry();

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(FMCommand.class);
        registry.register(FMEditCommand.class);

        //

        this.formatRegistry.register("simple", new SimpleFMFormat());
        this.formatRegistry.register("simple-cover", new SimpleCoverFMFormat());
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

    //

    @Nonnull
    public FMFormatRegistry getFormatRegistry() {
        return this.formatRegistry;
    }
}
