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
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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

    private ShardManager api;

    private DiscordConfiguration configuration;

    // ===

    @Nonnull
    public ShardManager getAPI() {
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
                public void onGenericEvent(Event event) {
                    relayEvent(event);
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
        CyNoodle.get().getEvents()
                .post(DiscordEvent.wrap(event));
    }

}