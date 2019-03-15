/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:moderation")
@MRequires("base:commands")
public final class ModerationModule extends Module {
    private ModerationModule() {}

    // ===

    public final static EntityType<Strike> TYPE_STRIKE = EntityType.of(Strike.class);
    public final static EntityType<StrikeSettings> TYPE_STRIKE_SETTINGS = EntityType.of(StrikeSettings.class);

    //

    private StrikeManager strikeManager;
    private GEntityManager<StrikeSettings> strikeSettingsManager;

    // ===

    @Override
    protected void start() {
        super.start();

        //

        this.strikeManager = new StrikeManager();
        this.strikeSettingsManager = new GEntityManager<>(TYPE_STRIKE_SETTINGS);

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(StrikeListCommand.class);
        registry.register(StrikeAddCommand.class);
        registry.register(StrikeRemoveCommand.class);
        registry.register(StrikeRestoreCommand.class);
        registry.register(StrikeEditCommand.class);
        registry.register(StrikeViewCommand.class);

        //

        this.strikeManager.ensureIndexes();
        this.strikeSettingsManager.ensureIndexes();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public StrikeManager getStrikeManager() {
        return this.strikeManager;
    }

    @Nonnull
    public GEntityManager<StrikeSettings> getStrikeSettingsManager() {
        return this.strikeSettingsManager;
    }
}