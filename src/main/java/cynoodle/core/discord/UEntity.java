/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import com.mongodb.client.model.Filters;
import cynoodle.core.entities.Entity;
import cynoodle.core.mongo.BsonDataException;
import net.dv8tion.jda.core.entities.User;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An Entity which belongs to a {@link User}.
 */
public abstract class UEntity extends Entity implements UHolder {
    protected UEntity() {}

    // ===

    private static final String KEY_USER = "user";

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
        return filterUser(DiscordPointer.to(user));
    }

    // == DATA ==

    @Override
    public void fromBson(@Nonnull BsonDocument source) throws BsonDataException {
        super.fromBson(source);

        Optional.of(source.get(KEY_USER)).ifPresent(v -> this.user = v.isInt64() ? DiscordPointer.to(v.asInt64().getValue()) : null);
    }

    @Nonnull
    @Override
    public BsonDocument toBson() throws BsonDataException {
        BsonDocument data = super.toBson();

        data.put(KEY_USER, this.user == null ? new BsonNull() : new BsonInt64(this.user.getID()));

        return data;
    }

}