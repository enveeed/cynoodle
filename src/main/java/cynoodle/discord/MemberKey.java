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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Immutable compound key to identify {@link Member Members},
 * consisting of a {@link Guild} and a {@link Member}.
 *
 * This is intended to be used internally only, e.g. as map keys.
 */
public final class MemberKey {

    private final long guild;
    private final long user;

    // ===

    private MemberKey(long guild, long user) {
        this.guild = guild;
        this.user = user;
    }

    // ===

    public long getGuildID() {
        return this.guild;
    }

    public long getUserID() {
        return this.user;
    }

    //

    @Nonnull
    public DiscordPointer getGuild() {
        return DiscordPointer.to(this.guild);
    }

    @Nonnull
    public DiscordPointer getUser() {
        return DiscordPointer.to(this.user);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberKey memberKey = (MemberKey) o;

        if (guild != memberKey.guild) return false;
        return user == memberKey.user;
    }

    @Override
    public int hashCode() {
        int result = (int) (guild ^ (guild >>> 32));
        result = 31 * result + (int) (user ^ (user >>> 32));
        return result;
    }

    // ===

    @Override
    public String toString() {
        return this.guild + ":" + this.user;
    }

    // ===

    @Nonnull
    public static MemberKey of(long guild, long user) {
        if(guild <= 0) throw new IllegalArgumentException("Illegal snowflake guild: " + guild);
        if(user <= 0) throw new IllegalArgumentException("Illegal snowflake guild: " + user);
        return new MemberKey(guild, user);
    }

    @Nonnull
    public static MemberKey of(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return of(guild.getID(), user.getID());
    }

    @Nonnull
    public static MemberKey of(@Nonnull Guild guild, @Nonnull User user) {
        return of(guild.getIdLong(), user.getIdLong());
    }

    @Nonnull
    public static MemberKey of(@Nonnull Member member) {
        return of(member.getGuild().getIdLong(), member.getUser().getIdLong());
    }
}
