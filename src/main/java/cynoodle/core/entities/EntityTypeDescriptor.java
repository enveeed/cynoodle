/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import com.google.common.flogger.FluentLogger;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import cynoodle.core.api.Checks;
import cynoodle.core.api.reflect.Annotations;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Descriptor for an {@link Entity} class and its {@link EntityType}.
 */
public final class EntityTypeDescriptor {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final Class<? extends Entity> entityClass;

    // ===

    private final String identifier;
    private final String collection;
    private final Set<IndexModel> indexes;

    // ===

    private EntityTypeDescriptor(@Nonnull Builder builder) {
        this.entityClass = builder.entityClass;

        this.identifier = Checks.notNull(builder.identifier, "identifier");
        this.collection = Checks.notNull(builder.collection, "collection");
        this.indexes = Checks.notNull(builder.indexes, "indexes");
    }

    // ===

    @Nonnull
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    //

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public String getCollection() {
        return collection;
    }

    @Nonnull
    public Set<IndexModel> getIndexes() {
        return this.indexes;
    }

    // ===

    /**
     * Used to collect the entity class properties while parsing
     */
    private static class Builder {

        private Class<? extends Entity> entityClass;

        //

        private String identifier;
        private String collection;
        private Set<IndexModel> indexes;

        // ======

        @Nonnull
        Builder setEntityClass(@Nonnull Class<? extends Entity> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        @Nonnull
        public Builder setIdentifier(@Nonnull String identifier) {
            this.identifier = identifier;
            return this;
        }

        @Nonnull
        public Builder setCollection(@Nonnull String collection) {
            this.collection = collection;
            return this;
        }

        @Nonnull
        public Builder setIndexes(@Nonnull Set<IndexModel> indexes) {
            this.indexes = indexes;
            return this;
        }
    }

    // ===

    /**
     * Parse the given {@link Entity} class into a {@link EntityTypeDescriptor}.
     * @param entityClass the entity class to parse
     * @return the parsed descriptor
     * @throws EntityClassException if the entity class could not be parsed
     */
    @Nonnull
    static EntityTypeDescriptor parse(@Nonnull Class<? extends Entity> entityClass) throws EntityClassException {

        Builder builder = new Builder();

        builder.setEntityClass(entityClass);

        // ======

        // ensure class and constructor properties

        if(!Modifier.isFinal(entityClass.getModifiers())) throw new EntityClassException("Entity class is not final!");

        if(entityClass.getDeclaredConstructors().length != 1)
            throw new EntityClassException("Entity class requires exactly one constructor!");

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends Entity> constructor = entityClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new EntityClassException("Entity class constructor is not private!");

        } catch (NoSuchMethodException e) {
            throw new EntityClassException("Entity class does not contain a no-argument constructor!", e);
        }

        // ======

        // find annotations
        EIdentifier annIdentifier = entityClass.getAnnotation(EIdentifier.class);
        ECollection annCollection = entityClass.getAnnotation(ECollection.class);
        Set<EIndex> annsIndexes = Annotations.collect(entityClass, EIndex.class, Entity.class);

        // ensure required annotations
        if(annIdentifier == null) throw new EntityClassException("Entity class is missing @EIdentifier annotation!");

        // ======

        String identifier = annIdentifier.value();

        try {
            EntityType.validateIdentifier(identifier);
        } catch (IllegalArgumentException ex) {
            throw new EntityClassException("Illegal Entity type name!", ex);
        }

        builder.setIdentifier(identifier);

        // ======

        String collection = annCollection != null ? annCollection.value() : identifier;

        builder.setCollection(collection);

        // ======

        Set<IndexModel> indexes = new HashSet<>();

        System.out.println("found indexes annotations for: "+entityClass+" " +annsIndexes.size());

        for (EIndex annIndex : annsIndexes) {

            Bson index = Indexes.ascending(annIndex.value());
            IndexOptions options = new IndexOptions()
                    .unique(annIndex.unique())
                    .name(annIndex.value())
                    .sparse(true);

            indexes.add(new IndexModel(index, options));
        }

        builder.setIndexes(Collections.unmodifiableSet(indexes));

        // ======

        return new EntityTypeDescriptor(builder);
    }
}
