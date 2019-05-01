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

import com.mongodb.client.model.Filters;
import cynoodle.entities.EIndex;
import cynoodle.entities.Entity;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cynoodle.discord.DiscordPointer.*;

/**
 * An Entity which belongs to a {@link Member}.
 */
@EIndex(MEntity.KEY_GUILD)
@EIndex(MEntity.KEY_USER)
public abstract class MEntity extends Entity implements IMEntity {
    protected MEntity() {}

    // ===

    static final String KEY_GUILD = "guild";
    static final String KEY_USER = "user";

    // ===

    /**
     * The Guild.
     */
    @Nullable
    private DiscordPointer guild = null;

    /**
     * The User.
     */
    @Nullable
    private DiscordPointer user = null;

    // == GUILD ==

    @Nonnull
    @Override
    public final Optional<DiscordPointer> getGuild() {
        return Optional.ofNullable(this.guild);
    }

    @Override
    public final void setGuild(@Nullable DiscordPointer guild) {
        this.guild = guild;
    }

    // === USER ===

    @Nonnull
    @Override
    public final Optional<DiscordPointer> getUser() {
        return Optional.ofNullable(this.user);
    }

    @Override
    public final void setUser(@Nullable DiscordPointer user) {
        this.user = user;
    }

    // === MEMBER ===

    public final void setMember(@Nullable DiscordPointer guild, @Nullable DiscordPointer user) {
        this.guild = guild;
        this.user = user;
    }

    // === FILTER ===

    @Nonnull
    public static Bson filterGuild(@Nonnull DiscordPointer pointer) {
        return Filters.eq(KEY_GUILD, pointer.getID());
    }

    @Nonnull
    public static Bson filterUser(@Nonnull DiscordPointer pointer) {
        return Filters.eq(KEY_USER, pointer.getID());
    }

    @Nonnull
    public static Bson filterMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return Filters.and(filterGuild(guild), filterUser(user));
    }

    //

    @Nonnull
    public static Bson filterGuild(@Nonnull Guild guild) {
        return filterGuild(to(guild));
    }

    @Nonnull
    public static Bson filterUser(@Nonnull User user) {
        return filterUser(to(user));
    }

    @Nonnull
    public static Bson filterMember(@Nonnull Member member) {
        return filterMember(to(member.getGuild()), to(member.getUser()));
    }

    // ===

    @Override
    public String toString() {
        return "MEntity(G:" + this.guild + ", U:" + this.user + ")";
    }


    // === DATA ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {

        this.guild = source.getAt(KEY_GUILD).as(fromBsonNullable()).or(this.guild);
        this.user = source.getAt(KEY_USER).as(fromBsonNullable()).or(this.user);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt(KEY_GUILD).as(toBsonNullable()).to(this.guild);
        data.setAt(KEY_USER).as(toBsonNullable()).to(this.user);

        return data;
    }
}
