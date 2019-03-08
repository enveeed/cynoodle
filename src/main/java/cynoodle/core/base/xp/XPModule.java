/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import com.google.common.eventbus.Subscribe;
import cynoodle.core.base.command.CommandModule;
import cynoodle.core.base.command.CommandRegistry;
import cynoodle.core.base.condition.Condition;
import cynoodle.core.base.condition.ConditionModule;
import cynoodle.core.discord.DiscordEvent;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.entities.embed.EmbedType;
import cynoodle.core.entities.embed.EmbedTypeRegistry;
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
@MRequires("base:command")
@MRequires("base:condition")
public final class XPModule extends Module {
    private XPModule() {}

    // ===

    private final static EntityType<XP> TYPE_XP = EntityType.of(XP.class);
    private final static EntityType<Rank> TYPE_RANK = EntityType.of(Rank.class);
    private final static EntityType<XPSettings> TYPE_SETTINGS = EntityType.of(XPSettings.class);

    public final static EmbedType<XPCondition> TYPE_CONDITION_XP = EmbedType.of("base:xp:xp", XPCondition.class);

    // ===

    private final XPFormula formula = new StandardXPFormula(); // TODO configurable

    private MEntityManager<XP> xpManager;
    private RankManager rankManager;
    private GEntityManager<XPSettings> settingsManager;

    private LeaderBoardManager leaderBoardManager;

    // TODO replace with cache
    final Map<DiscordPointer, XPStatus> status = new HashMap<>();

    private final XPEventHandler handler = new XPEventHandler(this);

    // ===

    @Override
    protected void start() {
        super.start();

        this.xpManager = new MEntityManager<>(TYPE_XP);
        this.rankManager = new RankManager(TYPE_RANK);
        this.settingsManager = new GEntityManager<>(TYPE_SETTINGS);

        leaderBoardManager = new LeaderBoardManager();

        //

        CommandRegistry registry = Module.get(CommandModule.class).getRegistry();

        registry.register(XPCommand.class);
        registry.register(XPAddCommand.class);
        registry.register(XPRemoveCommand.class);
        registry.register(XPTransferCommand.class);
        registry.register(LeaderBoardCommand.class);
        registry.register(RanksCommand.class);

        //

        EmbedTypeRegistry<Condition> types = Module.get(ConditionModule.class).getConditionTypes();

        types.register(TYPE_CONDITION_XP);

        //

        this.xpManager.ensureIndexes();
        this.rankManager.ensureIndexes();
        this.settingsManager.ensureIndexes();

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
}
