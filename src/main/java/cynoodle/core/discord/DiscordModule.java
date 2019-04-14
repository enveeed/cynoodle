/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.google.common.flogger.FluentLogger;
import cynoodle.core.Configuration;
import cynoodle.core.CyNoodle;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MSystem;
import cynoodle.core.module.Module;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

        LOG.atFine().log("Modifying JDA logging ...");

        try {
            setupJDALogging();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    private static void setupJDALogging() throws Exception {

        Field field = JDALogger.class.getDeclaredField("SLF4J_ENABLED");

        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, true);
    }

}