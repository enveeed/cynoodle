/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
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

        this.guild = source.getAt(KEY_GUILD).as(fromBsonNullable()).or(this.guild);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt(KEY_GUILD).as(toBsonNullable()).to(this.guild);

        return data;
    }
}
