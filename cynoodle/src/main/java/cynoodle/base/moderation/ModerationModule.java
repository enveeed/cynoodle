/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.moderation;

import cynoodle.base.commands.CommandRegistry;
import cynoodle.base.commands.CommandsModule;
import cynoodle.base.moderation.commands.*;
import cynoodle.util.concurrent.Schedules;
import cynoodle.util.concurrent.Service;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.entity.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

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

    private MEntityManager<Strike>         strikeEntityManager;
    private GEntityManager<StrikeSettings>  strikeSettingsEntityManager;
    private MEntityManager<MuteStatus>      muteStatusEntityManager;
    private GEntityManager<MuteSettings>    muteSettingsEntityManager;

    private Service muteApplyService;

    //

    private ModerationController controller;

    private StrikeManager strikeManager;
    private StrikeSettingsManager strikeSettingsManager;

    // ===

    @Override
    protected void start() {
        super.start();

        //

        this.strikeEntityManager            = new MEntityManager<>(ENTITY_STRIKE);
        this.strikeSettingsEntityManager    = new GEntityManager<>(ENTITY_STRIKE_SETTINGS);
        this.muteStatusEntityManager        = new MEntityManager<>(ENTITY_MUTE_STATUS);
        this.muteSettingsEntityManager      = new GEntityManager<>(ENTITY_MUTE_SETTINGS);

        //

        this.controller                     = new ModerationController();

        this.strikeManager                  = new StrikeManager(this.strikeEntityManager);
        this.strikeSettingsManager          = new StrikeSettingsManager(this.strikeSettingsEntityManager);

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

    // === MANAGERS ===

    @Nonnull
    MEntityManager<Strike> getStrikeEntities() {
        return this.strikeEntityManager;
    }

    @Nonnull
    GEntityManager<StrikeSettings> getStrikeSettingsEntities() {
        return this.strikeSettingsEntityManager;
    }

    @Nonnull
    MEntityManager<MuteStatus> getMuteStatusEntities() {
        return this.muteStatusEntityManager;
    }

    @Nonnull
    GEntityManager<MuteSettings> getMuteSettingsEntities() {
        return this.muteSettingsEntityManager;
    }

    // === PUBLIC ===

    @Nonnull
    public ModerationController controller() {
        return this.controller;
    }

    //

    /**
     * Get the manager for {@link Strike Strikes}.
     * @return the strike manager
     */
    @Nonnull
    public StrikeManager getStrikeManager() {
        return this.strikeManager;
    }

    /**
     * Get the manager for {@link StrikeSettings}.
     * @return the strike settings manager
     */
    public StrikeSettingsManager getStrikeSettingsManager() {
        return strikeSettingsManager;
    }
}