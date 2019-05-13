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

package cynoodle.discord;

import com.google.common.flogger.FluentLogger;
import cynoodle.Configuration;
import cynoodle.CyNoodle;
import cynoodle.module.MIdentifier;
import cynoodle.module.MSystem;
import cynoodle.module.Module;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

/**
 * This module manages all Discord API (JDA) functionality.
 */
@MIdentifier("system:discord")
@MSystem
public final class DiscordModule extends Module {
    private DiscordModule() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    /**
     * The account ID of the test account.
     */
    private static final long ID_TEST_ACCOUNT = 401357678053556225L;

    // ===

    private ShardManager api;

    private DiscordConfiguration configuration;

    // ===

    @Nonnull
    public ShardManager getAPI() {
        if(this.api == null) throw new IllegalStateException("Discord is not connected, cannot use API.");
        return this.api;
    }

    //

    @Nonnull
    public DiscordConfiguration getConfiguration() {
        return this.configuration;
    }

    // ===

    @Override
    protected void start() {
        super.start();

        LOG.atInfo().log("Connecting to Discord ...");

        // === CONFIGURATION ===

        LOG.atFine().log("Loading configuration ...");

        Configuration.Section section = CyNoodle.get().getConfiguration().get("discord")
                .orElseThrow(() -> new RuntimeException("Discord configuration is missing! (section 'discord')"));

        this.configuration = DiscordConfiguration.parse(section);

        // === SETUP ==

        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();

        LOG.atFine().log("Setting up connection ...");

        try {

            // token
            builder.setToken(this.configuration.getToken());

            // events
            builder.addEventListeners(new ListenerAdapter() {
                @Override
                public void onGenericEvent(@Nonnull GenericEvent event) {
                    relayEvent((Event) event);
                }
            });

            // TODO setup event listeners, games, etc ....

        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex); // TODO exception
        }

        LOG.atFine().log("Connecting ...");

        ShardManager manager;

        // === LOGIN ===

        try {
            // starts the login process
            manager = builder.build();
        } catch (LoginException ex) {
            throw new RuntimeException(ex); // TODO exception
        }

        this.api = manager;

    }

    //

    @Override
    protected void shutdown() {
        super.shutdown();

        LOG.atInfo().log("Disconnecting Discord ...");

        this.api.shutdown();
    }

    // === EVENTS ===

    private void relayEvent(@Nonnull Event event) {

        if(event instanceof GenericGuildEvent) {
            // discard event if the test instance is active on the server
            // and this is not the test instance
            Guild guild = ((GenericGuildEvent) event).getGuild();
            if(!isTestAccount() && (isTestAccountOn(guild) && isTestAccountOnline(guild)))
                return;
        }

        // relay event
        CyNoodle.get().getEvents().post(DiscordEvent.wrap(event));
    }

    // ===

    /**
     * Get the currently logged in Bot account.
     * @return the self account
     */
    @Nonnull
    public SelfUser getSelf() {
        return getAPI().getShardById(0).getSelfUser();
    }

    // === TEST ACCOUNT ===

    /**
     * Check if the currently logged in account is {@link #ID_TEST_ACCOUNT}.
     * @return true if this is the test account, false otherwise
     */
    public boolean isTestAccount() {
        return getSelf().getIdLong() == ID_TEST_ACCOUNT;
    }

    /**
     * Check if {@link #ID_TEST_ACCOUNT} is online on the given guild.
     * @param guild the guild
     * @return true if online, false otherwise.
     */
    public boolean isTestAccountOnline(@Nonnull Guild guild) {
        return guild.getMemberById(ID_TEST_ACCOUNT).getOnlineStatus() == OnlineStatus.ONLINE;
    }

    /**
     * Check if {@link #ID_TEST_ACCOUNT} is a member on the given guild.
     * @param guild the guild
     * @return true if it is a member, false otherwise.
     */
    public boolean isTestAccountOn(@Nonnull Guild guild) {
        return guild.getMemberById(ID_TEST_ACCOUNT) != null;
    }
    
    // === UTIL ===

    // NOTE: This is no longer needed because of
    // https://github.com/DV8FromTheWorld/JDA/commit/405925e3ae69b3c9b3078d1b61652f0a99347357

    /*
    private static void setupJDALogging() throws Exception {

        Field field = JDALogger.class.getDeclaredField("SLF4J_ENABLED");

        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, true);
    }*/

}