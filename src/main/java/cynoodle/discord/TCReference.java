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
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * {@link DiscordReference} to a {@link TextChannel}.
 */
public final class TCReference extends DiscordReference {

    private TCReference(long id) {
        super(id);
    }

    // ===

    @Nonnull
    public Optional<TextChannel> getTextChannel() {
        TextChannel textChannel = Discord.get().getAPI()
                .getTextChannelById(this.getID());
        return Optional.ofNullable(textChannel);
    }

    @Nonnull
    public TextChannel requireTextChannel() throws NoSuchElementException {
        return getTextChannel().orElseThrow(() -> new NoSuchElementException("There is no TextChannel with ID " + this.getID() + "!"));
    }

    // ===

    @Nonnull
    public static TCReference to(long id) {
        return new TCReference(id);
    }

    @Nonnull
    public static TCReference to(@Nonnull TextChannel textChannel) {
        return new TCReference(textChannel.getIdLong());
    }

    // ===

    @Nonnull
    public static Codec<TCReference> codec() {
        return new Codec<>() {
            @Override
            public TCReference load(BsonValue bson) throws BSONException {
                return TCReference.to(bson.asInt64().longValue());
            }

            @Override
            public BsonValue store(TCReference object) throws BSONException {
                return new BsonInt64(object.getID());
            }
        };
    }
}
