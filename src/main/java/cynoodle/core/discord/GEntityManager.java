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
