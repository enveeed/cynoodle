/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import cynoodle.core.mongo.IBson;

import javax.annotation.Nonnull;

/**
 * A sub-document for {@link Entity Entities}.
 */
public abstract class SubEntity implements IBson {
    protected SubEntity() {}

    // ===

    private SubEntityType<?> type;

    private Entity parent;

    // ===

    /**
     * Internal initialization method.
     * @param type the type
     * @param parent the parent entity
     */
    final void init(@Nonnull SubEntityType<?> type, @Nonnull Entity parent) {
        this.type = type;
        this.parent = parent;
    }

    // ===

    @Nonnull
    public final SubEntityType<?> getType() {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public final <E extends SubEntity> SubEntityType<E> getType(@Nonnull Class<E> eClass) {
        if(eClass == this.getType().getSubEntityClass())
            return (SubEntityType<E>) this.getType();
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
