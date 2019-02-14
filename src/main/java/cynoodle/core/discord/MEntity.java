/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.mongodb.client.model.Filters;
import cynoodle.core.entities.Entity;
import cynoodle.core.mongo.BsonDataException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An Entity which belongs to a {@link Member}.
 * Is both a {@link GHolder} and {@link UHolder} implementation.
 */
public abstract class MEntity extends Entity implements GHolder, UHolder {
    protected MEntity() {}

    // ===

    private static final String KEY_GUILD = "guild";
    private static final String KEY_USER = "user";

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

    @Nonnull
    public final DiscordPointer requireGuild() throws IllegalStateException {
        return getGuild().orElseThrow(() -> new IllegalStateException("No Guild set."));
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

    @Nonnull
    public final DiscordPointer requireUser() throws IllegalStateException {
        return getUser().orElseThrow(() -> new IllegalStateException("No User set."));
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
        return filterGuild(DiscordPointer.to(guild));
    }

    @Nonnull
    public static Bson filterUser(@Nonnull User user) {
        return filterUser(DiscordPointer.to(user));
    }

    @Nonnull
    public static Bson filterMember(@Nonnull Member member) {
        return filterMember(DiscordPointer.to(member.getGuild()), DiscordPointer.to(member.getUser()));
    }

    // === DATA ===

    @Override
    public void fromBson(@Nonnull BsonDocument source) throws BsonDataException {
        super.fromBson(source);

        Optional.of(source.get(KEY_GUILD)).ifPresent(v -> this.guild = v.isInt64() ? DiscordPointer.to(v.asInt64().getValue()) : null);
        Optional.of(source.get(KEY_USER)).ifPresent(v -> this.user = v.isInt64() ? DiscordPointer.to(v.asInt64().getValue()) : null);
    }

    @Nonnull
    @Override
    public BsonDocument toBson() throws BsonDataException {
        BsonDocument data = super.toBson();

        data.put(KEY_GUILD, this.guild == null ? new BsonNull() : new BsonInt64(this.guild.getID()));
        data.put(KEY_USER, this.user == null ? new BsonNull() : new BsonInt64(this.user.getID()));

        return data;
    }
}
