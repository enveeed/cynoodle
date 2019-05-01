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
import cynoodle.api.Snowflake;
import cynoodle.entities.EntityManager;
import cynoodle.entities.EntityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Convenience {@link EntityManager} specifically for {@link MEntity MEntities}.
 * @param <E> the entity type
 */
public class MEntityManager<E extends MEntity> extends EntityManager<E> {

    public MEntityManager(@Nonnull EntityType<E> type, @Nonnull Snowflake snowflake) {
        super(type, snowflake);
    }

    public MEntityManager(@Nonnull EntityType<E> type) {
        super(type);
    }

    // ===

    @Nonnull
    public final E create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user, @Nonnull Consumer<E> action) {
        return this.create(((Consumer<E>) e -> e.setMember(guild, user)).andThen(action));
    }

    @Nonnull
    public final E create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return this.create(guild, user, e -> {});
    }

    @Nonnull
    public final E create(@Nonnull Guild guild, @Nonnull User user, @Nonnull Consumer<E> action) {
        return this.create(DiscordPointer.to(guild), DiscordPointer.to(user) , action);
    }

    @Nonnull
    public final E create(@Nonnull Guild guild, @Nonnull User user) {
        return this.create(guild, user, e -> {});
    }

    @Nonnull
    public final E create(@Nonnull Member member, @Nonnull Consumer<E> action) {
        return this.create(member.getGuild(), member.getUser(), action);
    }

    @Nonnull
    public final E create(@Nonnull Member member) {
        return this.create(member, e -> {});
    }

    //

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(Filters.and(MEntity.filterMember(guild, user), filter),
                ((Consumer<E>) e -> e.setMember(guild, user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(MEntity.filterMember(guild, user),
                ((Consumer<E>) e -> e.setMember(guild, user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return this.firstOrCreate(guild, user, e -> {});
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild, @Nonnull User user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(guild), DiscordPointer.to(user), filter, action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild, @Nonnull User user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(guild), DiscordPointer.to(user), action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild, @Nonnull User user) {
        return this.firstOrCreate(guild, user, e -> {});
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Member member, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(member.getGuild(), member.getUser(), action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Member member) {
        return this.firstOrCreate(member, e -> {});
    }

}
