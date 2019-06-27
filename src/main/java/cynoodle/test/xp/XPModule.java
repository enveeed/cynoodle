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

package cynoodle.test.xp;

import cynoodle.test.commands.CommandRegistry;
import cynoodle.test.commands.CommandsModule;
import cynoodle.test.notifications.NotificationType;
import cynoodle.test.notifications.NotificationTypeRegistry;
import cynoodle.test.notifications.NotificationsModule;
import cynoodle.test.xp.commands.*;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.entity.EntityType;
import cynoodle.module.annotations.Identifier;
import cynoodle.module.annotations.Requires;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:xp</code>
 */
@Identifier("base:xp")
@Requires("base:commands")
@Requires("base:notifications")
public final class XPModule extends Module {
    private XPModule() {}

    // ===

    private final static EntityType<XPStatus> ENTITY_XP_STATUS
            = EntityType.of(XPStatus.class);
    private final static EntityType<Rank> ENTITY_RANK
            = EntityType.of(Rank.class);
    private final static EntityType<XPSettings> ENTITY_XP_SETTINGS
            = EntityType.of(XPSettings.class);

    final static NotificationType NOTIFICATION_LEVEL_UP = NotificationType.of("base:xp:level_up",
            "member", "level");
    final static NotificationType NOTIFICATION_LEVEL_DOWN = NotificationType.of("base:xp:level_down",
            "member", "level");
    final static NotificationType NOTIFICATION_RANK_UP = NotificationType.of("base:xp:rank_up",
            "member", "rank");
    final static NotificationType NOTIFICATION_XP_BOMB = NotificationType.of("base:xp:bomb",
            "member", "size");

    // ===

    /**
     * The XP formula (in the future, this will be replaced with a registry)
     */
    private final XPFormula formula = new StandardXPFormula();

    private MEntityManager<XPStatus> xpStatusEntityManager;
    private GEntityManager<XPSettings> xpSettingsEntityManager;
    private GEntityManager<Rank> rankEntityManager;

    //

    private RankManager rankManager;

    //

    private LeaderBoardManager leaderBoardManager;

    //

    private XPController controller;
    private XPEventHandler handler;

    // ===

    @Override
    protected void start() {
        super.start();

        this.xpStatusEntityManager = new MEntityManager<>(ENTITY_XP_STATUS);
        this.xpSettingsEntityManager = new GEntityManager<>(ENTITY_XP_SETTINGS);
        this.rankEntityManager = new GEntityManager<>(ENTITY_RANK);

        //

        this.rankManager = new RankManager(this.rankEntityManager);

        //

        leaderBoardManager = new LeaderBoardManager();

        //

        CommandRegistry commandRegistry = Module.get(CommandsModule.class).getRegistry();

        commandRegistry.register(XPCommand.class);
        commandRegistry.register(XPAddCommand.class);
        commandRegistry.register(XPRemoveCommand.class);
        commandRegistry.register(XPTransferCommand.class);
        commandRegistry.register(LeaderBoardCommand.class);
        commandRegistry.register(RanksCommand.class);
        commandRegistry.register(XPInfoCommand.class);
        commandRegistry.register(ForXPCommand.class);
        commandRegistry.register(ForLevelCommand.class);

        //

        NotificationTypeRegistry notificationRegistry = Module.get(NotificationsModule.class).getRegistry();

        notificationRegistry.register(NOTIFICATION_LEVEL_UP);
        notificationRegistry.register(NOTIFICATION_LEVEL_DOWN);
        notificationRegistry.register(NOTIFICATION_RANK_UP);
        notificationRegistry.register(NOTIFICATION_XP_BOMB);

        //

        this.controller = new XPController();

        //

        this.handler = new XPEventHandler();

        //

        this.registerListener(this.handler);

    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // === ENTITIES ===

    @Nonnull
    MEntityManager<XPStatus> getXPStatusEntityManager() {
        return this.xpStatusEntityManager;
    }

    @Nonnull
    GEntityManager<XPSettings> getXPSettingsEntityManager() {
        return this.xpSettingsEntityManager;
    }

    @Nonnull
    GEntityManager<Rank> getRankEntityManager() {
        return this.rankEntityManager;
    }

    // ===

    @Nonnull
    public XPFormula getFormula() {
        return this.formula;
    }

    //

    /**
     * Get the XP status for the given member.
     * @param guild the guild
     * @param user the user
     * @return the XP status
     */
    @Nonnull
    public XPStatus getStatus(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return this.xpStatusEntityManager.firstOrCreate(guild, user);
    }

    /**
     * Get the XP settings for the given guild.
     * @param guild the guild
     * @return the XP settings
     */
    @Nonnull
    public XPSettings getSettings(@Nonnull DiscordPointer guild) {
        return this.xpSettingsEntityManager.firstOrCreate(guild);
    }

    //

    /**
     * Get the {@link RankManager}.
     * @return the rank manager
     */
    @Nonnull
    public RankManager getRanks() {
        return this.rankManager;
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

    // ===

    @Nonnull
    public static XPModule get() {
        return get(XPModule.class);
    }
}
