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
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cynoodle.discord.DiscordPointer.*;

/**
 * An Entity which belongs to a {@link Guild}.
 */
@EIndex(GEntity.KEY_GUILD)
public abstract class GEntity extends Entity implements IGEntity {
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

    // === FILTER ===

    @Nonnull
    public static Bson filterGuild(@Nonnull DiscordPointer pointer) {
        return Filters.eq(KEY_GUILD, pointer.getID());
    }

    @Nonnull
    public static Bson filterGuild(@Nonnull Guild guild) {
        return filterGuild(to(guild));
    }

    // ===

    @Override
    public String toString() {
        return "GEntity(G:" + this.guild + ")";
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
