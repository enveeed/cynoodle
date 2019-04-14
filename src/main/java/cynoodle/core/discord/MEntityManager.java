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
