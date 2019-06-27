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

import cynoodle.mongodb.fluent.Codec;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * {@link DiscordReference} to a {@link Guild}.
 */
public final class GReference extends DiscordReference {

    private GReference(long id) {
        super(id);
    }

    // ===

    @Nonnull
    public Optional<Guild> getGuild() {
        Guild guild = Discord.get().getAPI()
                .getGuildById(this.getID());
        return Optional.ofNullable(guild);
    }

    @Nonnull
    public Guild requireGuild() throws NoSuchElementException {
        return getGuild().orElseThrow(() -> new NoSuchElementException("There is no connected Guild with ID " + this.getID() + "!"));
    }

    // ===

    @Nonnull
    public static GReference to(long id) {
        return new GReference(id);
    }

    @Nonnull
    public static GReference to(@Nonnull Guild guild) {
        return new GReference(guild.getIdLong());
    }

    //

    @Nonnull
    public static GReference of(@Nonnull Member member) {
        return new GReference(member.getGuild().getIdLong());
    }

    // ===

    @Nonnull
    public static Codec<GReference> codec() {
        return new Codec<>() {
            @Override
            public GReference load(BsonValue bson) throws BSONException {
                return GReference.to(bson.asInt64().longValue());
            }

            @Override
            public BsonValue store(GReference object) throws BSONException {
                return new BsonInt64(object.getID());
            }
        };
    }
}
