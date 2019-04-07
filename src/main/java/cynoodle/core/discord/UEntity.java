/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.mongodb.client.model.Filters;
import cynoodle.core.entities.EIndex;
import cynoodle.core.entities.Entity;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.entities.User;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cynoodle.core.discord.DiscordPointer.*;

/**
 * An Entity which belongs to a {@link User}.
 */
@EIndex(UEntity.KEY_USER)
public abstract class UEntity extends Entity implements IUEntity {
    protected UEntity() {}

    // ===

    static final String KEY_USER = "user";

    // ===

    /**
     * The User.
     */
    @Nullable
    private DiscordPointer user = null;

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

    // === FILTER ===

    @Nonnull
    public static Bson filterUser(@Nonnull DiscordPointer pointer) {
        return Filters.eq(KEY_USER, pointer.getID());
    }

    @Nonnull
    public static Bson filterUser(@Nonnull User user) {
        return filterUser(to(user));
    }

    // ===

    @Override
    public String toString() {
        return "UEntity(U:" + this.user + ")";
    }

    // == DATA ==

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.user = source.getAt(KEY_USER).as(fromBsonNullable()).or(this.user);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_USER).as(toBsonNullable()).to(this.user);

        return data;
    }

}