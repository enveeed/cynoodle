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
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An Entity which belongs to a {@link Guild}.
 */
public abstract class GEntity extends Entity implements GHolder {
    protected GEntity() {}

    // ===

    private static final String KEY_GUILD = "guild";

    // ===

    /**
     * The Guild.
     */
    @Nullable
    private DiscordPointer guild = null;

    // === GUILD ===

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

    // === FILTER ===

    @Nonnull
    public static Bson filterGuild(@Nonnull DiscordPointer pointer) {
        return Filters.eq(KEY_GUILD, pointer.getID());
    }

    @Nonnull
    public static Bson filterGuild(@Nonnull Guild guild) {
        return filterGuild(DiscordPointer.to(guild));
    }

    // === DATA ===

    @Override
    public void fromBson(@Nonnull BsonDocument source) throws BsonDataException {
        super.fromBson(source);

        Optional.of(source.get(KEY_GUILD)).ifPresent(v -> this.guild = v.isInt64() ? DiscordPointer.to(v.asInt64().getValue()) : null);
    }

    @Nonnull
    @Override
    public BsonDocument toBson() throws BsonDataException {
        BsonDocument data = super.toBson();

        data.put(KEY_GUILD, this.guild == null ? new BsonNull() : new BsonInt64(this.guild.getID()));

        return data;
    }
}
