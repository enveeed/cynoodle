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

package cynoodle.entities;

import cynoodle.mongo.fluent.Codec;
import org.bson.BSONException;
import org.bson.BsonInt64;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Immutable reference to any Entity with a snowflake ID.
 * EntityReferences should not be part of public API and rather be used internally.
 * @param <E> the entity type
 */
public final class EntityReference<E extends Entity> {

    private final EntityManager<E> manager;
    private final long id;

    // ===

    EntityReference(@Nonnull EntityManager<E> manager, long id) {
        this.manager = manager;
        this.id = id;
    }

    // ===

    /**
     * Get the manager which manages the referenced Entity type.
     * @return the entity manager
     */
    @Nonnull
    public EntityManager<E> getManager() {
        return this.manager;
    }

    /**
     * Get the referenced snowflake ID.
     * @return the snowflake ID
     */
    public long getID() {
        return this.id;
    }

    // ===

    /**
     * Attempt to get the referenced Entity from the manager.
     * @return an optional containing the Entity if it exists, otherwise empty
     */
    @Nonnull
    public Optional<E> get() {
        return this.manager.get(id);
    }

    @Nonnull
    public E require() throws NoSuchElementException {
        return get().orElseThrow(() -> new NoSuchElementException("No such Entity with ID " + this.id));
    }

    /**
     * Check if the referenced Entity exists.
     * @return true if the entity exists, false otherwise
     */
    public boolean exists() {
        return this.manager.exists(id);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityReference<?> that = (EntityReference<?>) o;

        if (id != that.id) return false;
        return manager.equals(that.manager);
    }

    @Override
    public int hashCode() {
        int result = manager.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    // ===

    @Nonnull
    public static <E extends Entity> Function<EntityReference<E>, BsonValue> store() {
        return ref -> new BsonInt64(ref.getID());
    }

    @Nonnull
    public static <E extends Entity> Function<BsonValue, EntityReference<E>> load(@Nonnull EntityManager<E> manager) {
        return value -> manager.reference(value.asInt64().getValue());
    }

    // ===

    // TODO possibly improve so that manager reference is not
    //  needed? But probably not possible, rather improve as a whole
    public static <E extends Entity> Codec<EntityReference<E>> codecWith(@Nonnull EntityManager<E> manager) {
        return new Codec<>() {
            @Override
            public EntityReference<E> load(BsonValue bson) throws BSONException {
                return manager.reference(bson.asInt64().getValue());
            }

            @Override
            public BsonValue store(EntityReference<E> object) throws BSONException {
                return new BsonInt64(object.getID());
            }
        };
    }
}
