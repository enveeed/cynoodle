/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.mongodb.client.model.Filters;
import cynoodle.core.api.Snowflake;
import cynoodle.core.entities.EntityManager;
import cynoodle.core.entities.EntityType;
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
    public final E create(@Nonnull DiscordPointer user, @Nonnull Consumer<E> action) {
        return this.create(((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E create(@Nonnull DiscordPointer user) {
        return this.create(user, e -> {});
    }

    @Nonnull
    public final E create(@Nonnull User user, @Nonnull Consumer<E> action) {
        return this.create(DiscordPointer.to(user), action);
    }

    @Nonnull
    public final E create(@Nonnull User user) {
        return this.create(user, e -> {});
    }

    //

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(Filters.and(UEntity.filterUser(user), filter),
                ((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(UEntity.filterUser(user),
                ((Consumer<E>) e -> e.setUser(user)).andThen(action));
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull DiscordPointer user) {
        return this.firstOrCreate(user, e -> {});
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user, @Nonnull Bson filter, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(user), filter, action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user, @Nonnull Consumer<E> action) {
        return this.firstOrCreate(DiscordPointer.to(user), action);
    }

    @Nonnull
    public final E firstOrCreate(@Nonnull User user) {
        return this.firstOrCreate(user, e -> {});
    }

    //

    @Nonnull
    public final Stream<E> stream(@Nonnull DiscordPointer user, @Nonnull Bson filter) {
        return stream(Filters.and(UEntity.filterUser(user), filter));
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull DiscordPointer user) {
        return stream(user, DEFAULT_FILTER);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull User user, @Nonnull Bson filter) {
        return stream(DiscordPointer.to(user), filter);
    }

    @Nonnull
    public final Stream<E> stream(@Nonnull User user) {
        return stream(user, DEFAULT_FILTER);
    }
}
