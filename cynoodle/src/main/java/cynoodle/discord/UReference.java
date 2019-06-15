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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * {@link DiscordReference} to a {@link User}.
 */
public final class UReference extends DiscordReference {

    private UReference(long id) {
        super(id);
    }

    // ===

    @Nonnull
    public Optional<User> getUser() {
        try {
            User user = Discord.get().getAPI()
                    .retrieveUserById(this.getID()).complete();
            return Optional.of(user);
        } catch (ErrorResponseException e) {
            if(e.getErrorResponse() == ErrorResponse.UNKNOWN_USER) return Optional.empty();
            else throw new RuntimeException("Unexpected error response while retrieving User!", e);
        }
    }

    @Nonnull
    public User requireUser() throws NoSuchElementException {
        return getUser().orElseThrow(() -> new NoSuchElementException("There is no User with ID " + this.getID() + "!"));
    }

    // ===

    @Nonnull
    public static UReference to(long id) {
        return new UReference(id);
    }

    @Nonnull
    public static UReference to(@Nonnull User user) {
        return new UReference(user.getIdLong());
    }

    //

    @Nonnull
    public static UReference of(@Nonnull Member member) {
        return new UReference(member.getUser().getIdLong());
    }

    // ===

    @Nonnull
    public static Codec<UReference> codec() {
        return new Codec<>() {
            @Override
            public UReference load(BsonValue bson) throws BSONException {
                return UReference.to(bson.asInt64().longValue());
            }

            @Override
            public BsonValue store(UReference object) throws BSONException {
                return new BsonInt64(object.getID());
            }
        };
    }
}
