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
import net.dv8tion.jda.core.entities.Guild;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cynoodle.core.discord.DiscordPointer.*;

/**
 * An Entity which belongs to a {@link Guild}.
 */
@EIndex(GEntity.KEY_GUILD)
public abstract class GEntity extends Entity implements GHolder {
    protected GEntity() {}

    // ===

    static final String KEY_GUILD = "guild";

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
        return filterGuild(to(guild));
    }

    // === DATA ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.guild = source.getAt(KEY_GUILD).as(fromBsonNullable()).or(this.guild);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_GUILD).as(toBsonNullable()).to(this.guild);

        return data;
    }
}
