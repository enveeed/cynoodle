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
import net.dv8tion.jda.api.entities.Role;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * {@link DiscordReference} to a {@link Role}.
 */
public final class RReference extends DiscordReference {

    private RReference(long id) {
        super(id);
    }

    // ===

    @Nonnull
    public Optional<Role> getRole() {
        Role role = Discord.get().getAPI()
                .getRoleById(this.getID());
        return Optional.ofNullable(role);
    }

    @Nonnull
    public Role requireRole() throws NoSuchElementException {
        return getRole().orElseThrow(() -> new NoSuchElementException("There is no Role with ID " + this.getID() + "!"));
    }

    // ===

    @Nonnull
    public static RReference to(long id) {
        return new RReference(id);
    }

    @Nonnull
    public static RReference to(@Nonnull Role role) {
        return new RReference(role.getIdLong());
    }

    // ===

    @Nonnull
    public static Codec<RReference> codec() {
        return new Codec<>() {
            @Override
            public RReference load(BsonValue bson) throws BSONException {
                return RReference.to(bson.asInt64().longValue());
            }

            @Override
            public BsonValue store(RReference object) throws BSONException {
                return new BsonInt64(object.getID());
            }
        };
    }
}
