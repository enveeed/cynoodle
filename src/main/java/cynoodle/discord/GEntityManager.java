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
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Convenience {@link EntityManager} specifically for {@link GEntity GEntities}.
 * @param <E> the entity type
 */
public class GEntityManager<E extends GEntity> extends EntityManager<E> {

    public GEntityManager(@Nonnull EntityType<E> type, @Nonnull Snowflake snowflake) {
        super(type, snowflake);
    }

    public GEntityManager(@Nonnull EntityType<E> type) {
        super(type);
    }

    // ===

    @Nonnull
    public final E create(@Nonnull DiscordPointer guild, @Nonnull Consumer<E> action) {
        return this.create(((Consumer<E>) e -> e.setGuild(guild)).andThen(action));
    }

    @Nonnull
    public final E create(@Nonnull DiscordPointer guild) {
        return this.create(guild, e -> {});
    }

    @Nonnull
    public final E create(@Nonnull Guild guild, @Nonnull Consumer<E> action) {
        return this.create(DiscordPointer.to(guild), action);
    }

    @Nonnull
    public final E create(@Nonnull Guild guild) {
        return this.create(guild, e -> {});
    }

    //

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(Filters.and(GEntity.filterGuild(guild), filter),
                ((Consumer<E>) e -> e.setGuild(guild)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(GEntity.filterGuild(guild),
                ((Consumer<E>) e -> e.setGuild(guild)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer guild) {
        return this.firstOrCreate(guild, e -> {});
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(guild), filter, action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(guild), action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull Guild guild) {
        return this.firstOrCreate(guild, e -> {});
    }

    //

    @Nonnull
    public final Stream<E> stream(@Nonnull DiscordPointer guild, @Nonnull Bson filter) {
        return stream(Filters.and(GEntity.filterGuild(guild), filter));
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull DiscordPointer guild) {
        return stream(guild, DEFAULT_FILTER);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull Guild guild, @Nonnull Bson filter) {
        return stream(DiscordPointer.to(guild), filter);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull Guild guild) {
        return stream(guild, DEFAULT_FILTER);
    }
}
