/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.events.Event;

import javax.annotation.Nonnull;

/**
 * Wrapper event for JDA / Discord API Events.
 */
public final class DiscordEvent implements Event {

    private final net.dv8tion.jda.core.events.Event event;

    // ===

    DiscordEvent(@Nonnull net.dv8tion.jda.core.events.Event event) {
        this.event = event;
    }

    // ===

    @Nonnull
    public <E extends net.dv8tion.jda.core.events.Event> E get(@Nonnull Class<E> eventClass) {
        return eventClass.cast(this.event);
    }

    @Nonnull
    public net.dv8tion.jda.core.events.Event get() {
        return this.get(net.dv8tion.jda.core.events.Event.class);
    }

    //

    public boolean is(@Nonnull Class<? extends net.dv8tion.jda.core.events.Event> eventClass) {
        return eventClass.isAssignableFrom(event.getClass());
    }
}
