/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.base.command.CommandModule;
import cynoodle.core.base.command.CommandRegistry;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:strikes")
@MRequires("base:command")
public final class StrikesModule extends Module {
    private StrikesModule() {}

    // ===

    public final static EntityType<Strike> TYPE_STRIKE = EntityType.of(Strike.class);
    public final static EntityType<StrikeSettings> TYPE_STRIKE_SETTINGS = EntityType.of(StrikeSettings.class);

    //

    private StrikeManager strikes;
    private GEntityManager<StrikeSettings> settings;

    // ===

    @Override
    protected void start() {
        super.start();

        //

        this.strikes = new StrikeManager();
        this.settings = new GEntityManager<>(TYPE_STRIKE_SETTINGS);

        //

        CommandRegistry registry = Module.get(CommandModule.class).getRegistry();

        registry.register(StrikeListCommand.class);
        registry.register(StrikeAddCommand.class);
        registry.register(StrikeRemoveCommand.class);
        registry.register(StrikeRestoreCommand.class);
        registry.register(StrikeEditCommand.class);
        registry.register(StrikeViewCommand.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public StrikeManager getStrikes() {
        return this.strikes;
    }

    @Nonnull
    public GEntityManager<StrikeSettings> getSettings() {
        return this.settings;
    }
}