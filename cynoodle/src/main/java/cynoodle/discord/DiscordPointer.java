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

import cynoodle.module.Module;
import cynoodle.mongodb.fluent.Codec;
import net.dv8tion.jda.api.entities.*;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Immutable pointer to any snowflake-identified entity within Discord.
 */
@Deprecated
public final class DiscordPointer {

    private final long id;

    // ===

    private DiscordPointer(long id) {
        this.id = id;
    }

    // ===

    /**
     * Get the snowflake ID of the entity this pointer points to.
     * @return the pointer target ID
     */
    public long getID() {
        return this.id;
    }

    // ===

    @Nonnull
    public Optional<User> asUser() {
        return Optional.ofNullable(Module.get(DiscordModule.class)
                .getAPI().getUserById(this.id));
    }

    @Nonnull
    public User requireUser() throws NoSuchElementException {
        return asUser().orElseThrow(() -> new NoSuchElementException("There is no User with ID " + this.id));
    }

    @Nonnull
    public Optional<Guild> asGuild() {
        return Optional.ofNullable(Module.get(DiscordModule.class)
                .getAPI().getGuildById(this.id));
    }

    @Nonnull
    public Guild requireGuild() throws NoSuchElementException {
        return asGuild().orElseThrow(() -> new NoSuchElementException("There is no Guild with ID " + this.id));
    }

    public Optional<Role> asRole() {
        return Optional.ofNullable(Module.get(DiscordModule.class)
                .getAPI().getRoleById(this.id));
    }

    @Nonnull
    public Role requireRole() throws NoSuchElementException {
        return asRole().orElseThrow(() -> new NoSuchElementException("There is no Role with ID " + this.id));
    }

    @Nonnull
    public Optional<TextChannel> asTextChannel() {
        return Optional.ofNullable(Module.get(DiscordModule.class)
                .getAPI().getTextChannelById(this.id));
    }

    @Nonnull
    public TextChannel requireTextChannel() throws NoSuchElementException {
        return asTextChannel().orElseThrow(() -> new NoSuchElementException("There is no TextChannel with ID " + this.id));
    }

    // ===

    @Nonnull
    public Optional<Member> asMember(@Nonnull Guild guild) {
        return asUser().map(guild::getMember);
    }

    @Nonnull
    public Optional<Member> asMember(@Nonnull DiscordPointer guild) {
        return guild.asGuild().flatMap(this::asMember);
    }

    // ===

    @Override
    public String toString() {
        return Long.toString(this.id);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscordPointer that = (DiscordPointer) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    // ===

    /**
     * Create a new pointer to the given snowflake ID.
     * @param snowflake the snowflake ID
     * @return a new pointer pointing to the given ID.
     * @throws IllegalArgumentException if the snowflake ID was malformed
     */
    @Nonnull
    public static DiscordPointer to(long snowflake) throws IllegalArgumentException {
        if(snowflake <= 0L) throw new IllegalArgumentException("Illegal snowflake ID: "+snowflake);
        return new DiscordPointer(snowflake);
    }

    /**
     * Create a new pointer to the snowflake ID of the given JDA {@link ISnowflake}.
     * @param entity the JDA {@link ISnowflake} with the ID.
     * @return a new pointer pointing to the ID of the given JDA entity
     * @throws IllegalArgumentException if the snowflake ID of the given entity was malformed
     */
    @Nonnull
    public static DiscordPointer to(@Nonnull ISnowflake entity) {
        return to(entity.getIdLong());
    }

    // ===

    public static Function<DiscordPointer, BsonInt64> toBson() {
        return pointer -> new BsonInt64(pointer.getID());
    }

    public static Function<DiscordPointer, BsonValue> toBsonNullable() {
        return pointer -> pointer == null ? BsonNull.VALUE : new BsonInt64(pointer.getID());
    }

    public static Function<BsonValue, DiscordPointer> fromBson() {
        return value -> DiscordPointer.to(value.asInt64().getValue());
    }

    public static Function<BsonValue, DiscordPointer> fromBsonNullable() {
        return value -> value.isNull() ? null : DiscordPointer.to(value.asInt64().getValue());
    }

    // ===

    public static Codec<DiscordPointer> codec() {
        return new Codec<>() {
            @Override
            public DiscordPointer load(BsonValue bson) throws BSONException {
                return fromBson().apply(bson);
            }

            @Override
            public BsonValue store(DiscordPointer object) throws BSONException {
                return toBson().apply(object);
            }
        };
    }
}
