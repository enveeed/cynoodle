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

import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Type information for a {@link NestedEntity} class.
 */
public final class NestedEntityType<E extends NestedEntity> {

    private final Class<E> eClass;

    // ===

    private NestedEntityType(@Nonnull Class<E> eClass) {
        this.eClass = eClass;
    }

    // ===

    @Nonnull
    public Class<E> getNestedEntityClass() {
        return this.eClass;
    }

    // ===

    @Nonnull
    public E create(@Nonnull Entity parent) {

        Constructor<E> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.getNestedEntityClass().getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        E entity;

        try {
            entity = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create new SubEntity instance!", e);
        }

        // initialize the sub entity
        entity.init(this, parent);

        //

        return entity;
    }

    @Nonnull
    public E create(@Nonnull Entity parent, @Nonnull Consumer<E> action) {
        E entity = create(parent);
        action.accept(entity);
        return entity;
    }

    @Nonnull
    public E createOf(@Nonnull Entity parent, @Nonnull FluentDocument source) throws BsonDataException {

        E instance = create(parent);

        instance.fromBson(source);

        return instance;
    }

    @Nonnull
    public E createOf(@Nonnull Entity parent, @Nonnull FluentDocument source, @Nonnull Consumer<E> action) {
        E entity = createOf(parent, source);
        action.accept(entity);
        return entity;
    }

    //

    @Nonnull
    public Function<BsonValue, E> load(@Nonnull Entity parent) {
        return value -> createOf(parent, FluentDocument.wrap(value.asDocument()));
    }

    @Nonnull
    public Function<E, BsonValue> store() {
        return entity -> entity.toBson().asBson();
    }

    // ===

    @Nonnull
    public static <E extends NestedEntity> NestedEntityType<E> of(@Nonnull Class<E> eClass) throws EntityClassException {

        if(!Modifier.isFinal(eClass.getModifiers())) throw new EntityClassException("SubEntity class is not final!");

        if(eClass.getDeclaredConstructors().length != 1)
            throw new EntityClassException("SubEntity class requires exactly one constructor!");

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends NestedEntity> constructor = eClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new EntityClassException("SubEntity class constructor is not private!");

            constructor.setAccessible(true);

        } catch (NoSuchMethodException | InaccessibleObjectException e) {
            throw new EntityClassException("SubEntity class does not contain a no-argument constructor or it could not be accessed!", e);
        }

        //

        return new NestedEntityType<>(eClass);
    }
}
