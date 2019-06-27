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

import javax.annotation.Nonnull;

/**
 * Wrapper event for JDA / Discord API Events.
 */
public final class DiscordEvent {

    private final net.dv8tion.jda.api.events.Event event;

    // ===

    private DiscordEvent(@Nonnull net.dv8tion.jda.api.events.Event event) {
        this.event = event;
    }

    // ===

    @Nonnull
    public <E extends net.dv8tion.jda.api.events.Event> E get(@Nonnull Class<E> eventClass) {
        return eventClass.cast(this.event);
    }

    @Nonnull
    public net.dv8tion.jda.api.events.Event get() {
        return this.get(net.dv8tion.jda.api.events.Event.class);
    }

    //

    public boolean is(@Nonnull Class<? extends net.dv8tion.jda.api.events.Event> eventClass) {
        return eventClass.isAssignableFrom(event.getClass());
    }

    // ===

    @Nonnull
    static DiscordEvent wrap(@Nonnull net.dv8tion.jda.api.events.Event event) {
        return new DiscordEvent(event);
    }
}
