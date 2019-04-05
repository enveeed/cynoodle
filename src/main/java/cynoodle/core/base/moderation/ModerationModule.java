/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.concurrent.Schedules;
import cynoodle.core.concurrent.Service;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;

@MIdentifier("base:moderation")
@MRequires("base:commands")
public final class ModerationModule extends Module {
    private ModerationModule() {}

    // ===

    final static EntityType<Strike>         ENTITY_STRIKE           = EntityType.of(Strike.class);
    final static EntityType<StrikeSettings> ENTITY_STRIKE_SETTINGS  = EntityType.of(StrikeSettings.class);
    final static EntityType<MuteStatus>     ENTITY_MUTE_STATUS      = EntityType.of(MuteStatus.class);
    final static EntityType<MuteSettings>   ENTITY_MUTE_SETTINGS    = EntityType.of(MuteSettings.class);

    // ===

    private StrikeManager                   strikeManager;
    private GEntityManager<StrikeSettings>  strikeSettingsManager;
    private MEntityManager<MuteStatus>      muteStatusManager;
    private GEntityManager<MuteSettings>    muteSettingsManager;

    private Service muteApplyService;

    private ModerationController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        //

        this.strikeManager = new StrikeManager();
        this.strikeSettingsManager = new GEntityManager<>(ENTITY_STRIKE_SETTINGS);
        this.muteStatusManager = new MEntityManager<>(ENTITY_MUTE_STATUS);
        this.muteSettingsManager = new GEntityManager<>(ENTITY_MUTE_SETTINGS);

        //

        this.controller = new ModerationController();

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(StrikeListCommand.class);
        registry.register(StrikeAddCommand.class);
        registry.register(StrikeRemoveCommand.class);
        registry.register(StrikeRestoreCommand.class);
        registry.register(StrikeEditCommand.class);
        registry.register(StrikeViewCommand.class);

        registry.register(MuteCommand.class);
        registry.register(UnMuteCommand.class);

        registry.register(BulkDeleteCommand.class);

        //

        this.muteApplyService = Service.of(() -> controller().applyMutes(),
                        Schedules.delay(1, ChronoUnit.MINUTES), noodle().pool());

        this.muteApplyService.start();
        this.muteApplyService.awaitStart();
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        this.muteApplyService.stop();
        this.muteApplyService.awaitStop();
    }

    // ===

    // TODO public for legacy
    @Nonnull
    public StrikeManager getStrikeManager() {
        return this.strikeManager;
    }

    @Nonnull
    GEntityManager<StrikeSettings> getStrikeSettingsManager() {
        return this.strikeSettingsManager;
    }

    @Nonnull
    MEntityManager<MuteStatus> getMuteStatusManager() {
        return this.muteStatusManager;
    }

    @Nonnull
    GEntityManager<MuteSettings> getMuteSettingsManager() {
        return this.muteSettingsManager;
    }

    // ===

    @Nonnull
    public ModerationController controller() {
        return this.controller;
    }
}