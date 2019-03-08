/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.Configuration;
import cynoodle.core.CyNoodle;
import cynoodle.core.base.command.CommandModule;
import cynoodle.core.base.command.CommandRegistry;
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
@MRequires("base:command")
public final class FMModule extends Module {
    private FMModule() {}

    // ===

    private static final EntityType<FM> TYPE_FM = EntityType.of(FM.class);

    // ===

    private FMConfiguration configuration;

    //

    private UEntityManager<FM> fmManager;

    // ===

    @Override
    protected void start() {
        super.start();

        Configuration.Section section = CyNoodle.get().getConfiguration().get("fm")
                .orElseThrow(() -> new RuntimeException("last.fm configuration is missing! (section 'fm')"));

        this.configuration = FMConfiguration.parse(section);

        //

        fmManager = new UEntityManager<>(TYPE_FM);

        //

        CommandRegistry registry = Module.get(CommandModule.class).getRegistry();

        registry.register(FMCommand.class);
        registry.register(FMProfileCommand.class);
        registry.register(FMEditCommand.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public FMConfiguration getConfiguration() {
        return this.configuration;
    }

    //

    @Nonnull
    public UEntityManager<FM> getFMManager() {
        return this.fmManager;
    }
}
