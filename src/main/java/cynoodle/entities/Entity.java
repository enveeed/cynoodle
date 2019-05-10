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

import cynoodle.CyNoodle;
import cynoodle.api.Base62;
import cynoodle.api.Snowflake;
import cynoodle.mongo.IBsonDocument;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * An Entity, a persistent (on MongoDB), uniquely identified (via snowflakes) and cached data object.
 */
public abstract class Entity implements IEntity, IBsonDocument {

    private EntityManager<?> manager;
    private long id;

    //

    /**
     * Internal initialization method for entity instances.
     * @param manager the entity manager
     * @param id the entity snowflake ID
     */
    final void init(@Nonnull EntityManager<?> manager, long id) {
        this.manager = manager;
        this.id = id;
    }

    // === ID ===

    /**
     * Get the snowflake ID of this Entity.
     * @return the ID
     */
    public final long getID() {
        return this.id;
    }

    /**
     * Get the creation time of this Entity.
     * @return an Instant of the creation time of this Entity
     * @see Snowflake
     * @see CyNoodle#CYNOODLE_EPOCH
     */
    @Nonnull
    public final Instant getCreationTime() {
        return this.manager.getSnowflake()
                .getCreationTime(this.id);
    }

    //

    @Nonnull
    @Override
    public final String getIDString() {
        return Long.toString(this.id);
    }

    @Nonnull
    @Override
    public String getIDStringBase62() {
        return Base62.toBase62(this.id);
    }

    // === MANAGER ===

    /**
     * Get the {@link EntityManager} for this Entity.
     * @return the manager
     */
    @Nonnull
    public EntityManager<?> getManager() {
        return this.manager;
    }

    /**
     * Get the {@link EntityManager} for this Entity, casted to
     * a useful type rather than using the wildcard. Given type must match
     * the actual entity class.
     * @param entityClass the entity class of this entity
     * @param <E> the entity type
     * @return the casted manager
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public final <E extends Entity> EntityManager<E> getManager(@Nonnull Class<E> entityClass) {
        if(entityClass == this.getType().getDescriptor().getEntityClass())
            return (EntityManager<E>) this.manager;
        else throw new IllegalArgumentException("Class does not equal the actual Entity class!");
    }

    // == TYPE ==

    /**
     * Get the {@link EntityType} of this Entity.
     * @return the EntityType
     */
    @Nonnull
    public final EntityType<?> getType() {
        return getManager().getType();
    }

    @Nonnull
    public final String getTypeIdentifier() {
        return getType().getIdentifier();
    }

    // ===

    @Nonnull
    public final EntityReference<?> reference() {
        return getManager().reference(this.id);
    }

    @Nonnull
    public final <E extends Entity> EntityReference<E> reference(@Nonnull Class<E> entityClass) {
        return getManager(entityClass).reference(this.id);
    }

    // ===

    @Override
    public String toString() {
        return getTypeIdentifier()+"-"+getID();
    }

    // === DATA ==

    public void persist() throws IllegalStateException, EntityIOException {
        this.manager.persist(this.id);
    }

    public void update() throws IllegalStateException, NoSuchElementException, EntityIOException {
        this.manager.update(this.id);
    }

    // ===

    /**
     * Delete this Entity. This is <code>protected</code> since
     * direct deletion should not be part of public API since it doesnt allow
     * Entity implementations to react to deletes accordingly.
     * In case this is desired, it should be overridden with a <code>public</code> method.
     * @throws NoSuchElementException if the Entity does not exist
     * @throws EntityIOException if there was an IO issue while deleting the Entity
     */
    protected void delete() throws NoSuchElementException, EntityIOException {
        this.manager.delete(this.id);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (id != entity.id) return false;
        return manager.equals(entity.manager);
    }

    @Override
    public int hashCode() {
        int result = manager.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}