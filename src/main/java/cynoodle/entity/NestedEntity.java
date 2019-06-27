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

package cynoodle.entity;

import cynoodle.mongodb.IBsonDocument;

import javax.annotation.Nonnull;

/**
 * A nested document for {@link Entity Entities}.
 */
public abstract class NestedEntity implements IBsonDocument {
    protected NestedEntity() {}

    // ===

    private NestedEntityType<?> type;

    private Entity parent;

    // ===

    /**
     * Internal initialization method.
     * @param type the type
     * @param parent the parent entity
     */
    final void init(@Nonnull NestedEntityType<?> type, @Nonnull Entity parent) {
        this.type = type;
        this.parent = parent;
    }

    // ===

    @Nonnull
    public final NestedEntityType<?> getType() {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public final <E extends NestedEntity> NestedEntityType<E> getType(@Nonnull Class<E> eClass) {
        if(eClass == this.getType().getNestedEntityClass())
            return (NestedEntityType<E>) this.getType();
        else throw new IllegalArgumentException("Class does not equal the actual SubEntity class!");
    }

    //

    /**
     * Get the parent entity.
     * @return the parent entity.
     */
    @Nonnull
    public final Entity getParent() {
        return this.parent;
    }
}
