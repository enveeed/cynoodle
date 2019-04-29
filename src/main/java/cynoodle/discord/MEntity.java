/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
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
        super.fromBson(source);

        this.guild = source.getAt(KEY_GUILD).as(fromBsonNullable()).or(this.guild);
        this.user = source.getAt(KEY_USER).as(fromBsonNullable()).or(this.user);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_GUILD).as(toBsonNullable()).to(this.guild);
        data.setAt(KEY_USER).as(toBsonNullable()).to(this.user);

        return data;
    }
}
