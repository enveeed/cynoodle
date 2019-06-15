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
import cynoodle.util.Snowflake;
import cynoodle.entity.EntityManager;
import cynoodle.entity.EntityType;
import net.dv8tion.jda.api.entities.User;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Convenience {@link EntityManager} specifically for {@link UEntity UEntities}.
 * @param <E> the entity type
 */
public class UEntityManager<E extends UEntity> extends EntityManager<E> {

    public UEntityManager(@Nonnull EntityType<E> type, @Nonnull Snowflake snowflake) {
        super(type, snowflake);
    }

    public UEntityManager(@Nonnull EntityType<E> type) {
        super(type);
    }

    // ===

    @Nonnull
    public final E create(@Nonnull UReference user, @Nonnull Consumer<E> action) {
        return this.create(((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E create(@Nonnull UReference user) {
        return this.create(user, e -> {});
    }

    @Nonnull
    public final E create(@Nonnull User user, @Nonnull Consumer<E> action) {
        return this.create(UReference.to(user), action);
    }

    @Nonnull
    public final E create(@Nonnull User user) {
        return this.create(user, e -> {});
    }

    //

    @Nonnull
    public final E firstOrCreate(@Nonnull UReference user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(Filters.and(UEntity.filterUser(user), filter),
                ((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull UReference user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(UEntity.filterUser(user),
                ((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull UReference user) {
        return this.firstOrCreate(user, e -> {});
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(UReference.to(user), filter, action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(UReference.to(user), action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user) {
        return this.firstOrCreate(user, e -> {});
    }

    //

    @Nonnull
    public final Stream<E> stream(@Nonnull UReference user, @Nonnull Bson filter) {
        return stream(Filters.and(UEntity.filterUser(user), filter));
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull UReference user) {
        return stream(user, DEFAULT_FILTER);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull User user, @Nonnull Bson filter) {
        return stream(UReference.to(user), filter);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull User user) {
        return stream(user, DEFAULT_FILTER);
    }
}
