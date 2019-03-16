/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.eventbus.Subscribe;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.notifications.NotificationType;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>base:xp</code>
 */
@MIdentifier("base:xp")
@MRequires("base:commands")
@MRequires("base:notifications")
public final class XPModule extends Module {
    private XPModule() {}

    // ===

    private final static EntityType<XP> ENTITY_XP = EntityType.of(XP.class);
    private final static EntityType<Rank> ENTITY_RANK = EntityType.of(Rank.class);
    private final static EntityType<XPSettings> ENTITY_SETTINGS = EntityType.of(XPSettings.class);

    final static NotificationType NOTIFICATION_LEVEL_UP = new NotificationType("base:xp:level_up",
            "{0} has reached **Level {1}**!");
    final static NotificationType NOTIFICATION_LEVEL_DOWN = new NotificationType("base:xp:level_down",
            "{0} has leveled down to **Level {1}**!");
    final static NotificationType NOTIFICATION_RANK_UP = new NotificationType("base:xp:rank_up",
            "{0} has reached the Rank **{1}**!");

    // ===

    private final XPFormula formula = new StandardXPFormula(); // TODO configurable

    private MEntityManager<XP> xpManager;
    private RankManager rankManager;
    private GEntityManager<XPSettings> settingsManager;

    private LeaderBoardManager leaderBoardManager;

    // TODO replace with cache
    final Map<DiscordPointer, XPStatus> status = new HashMap<>();

    private final XPEventHandler handler = new XPEventHandler(this);

    private XPController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        this.xpManager = new MEntityManager<>(ENTITY_XP);
        this.rankManager = new RankManager(ENTITY_RANK);
        this.settingsManager = new GEntityManager<>(ENTITY_SETTINGS);

        leaderBoardManager = new LeaderBoardManager();

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(XPCommand.class);
        registry.register(XPAddCommand.class);
        registry.register(XPRemoveCommand.class);
        registry.register(XPTransferCommand.class);
        registry.register(LeaderBoardCommand.class);
        registry.register(RanksCommand.class);

        //

        this.xpManager.ensureIndexes();
        this.rankManager.ensureIndexes();
        this.settingsManager.ensureIndexes();

        //

        this.controller = new XPController();

    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Subscribe
    private void onEvent(@Nonnull DiscordEvent event) {
        this.handler.onEvent(event);
    }

    // ===

    // TODO configurable
    @Nonnull
    public XPFormula getFormula() {
        return this.formula;
    }

    //

    @Nonnull
    public MEntityManager<XP> getXPManager() {
        return this.xpManager;
    }

    @Nonnull
    public RankManager getRankManager() {
        return this.rankManager;
    }

    @Nonnull
    public GEntityManager<XPSettings> getSettingsManager() {
        return this.settingsManager;
    }

    //

    @Nonnull
    public LeaderBoardManager getLeaderBoardManager() {
        return this.leaderBoardManager;
    }

    //

    @Nonnull
    public XPController controller() {
        return this.controller;
    }
}
