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

package cynoodle.base.profiles;

import cynoodle.discord.DiscordPointer;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class ProfileBadge {

    private final DiscordPointer emote;
    private final Predicate<User> predicate;

    // ===

    public ProfileBadge(@Nonnull DiscordPointer emote, @Nonnull Predicate<User> predicate) {
        this.emote = emote;
        this.predicate = predicate;
    }

    // ===

    @Nonnull
    public DiscordPointer getEmote() {
        return this.emote;
    }

    // ===

    public boolean test(@Nonnull User user) {
        return this.predicate.test(user);
    }
}
