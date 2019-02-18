/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities;

import com.google.common.collect.Streams;
import com.google.common.flogger.FluentLogger;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import cynoodle.core.CyNoodle;
import cynoodle.core.Snowflake;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.MongoModule;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A manager for {@link Entity Entities} of a specific type, using MongoDB.
 * @param <E> the entity type
 * @see Entity
 */
public class EntityManager<E extends Entity> {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ======

    private static final Bson DEFAULT_FILTER = new BsonDocument();

    private static final String KEY_ID = "id";

    private static final ReplaceOptions OPTIONS_REPLACE     = new ReplaceOptions().upsert(true);
    private static final DeleteOptions  OPTIONS_DELETE      = new DeleteOptions();

    private static final CountOptions   OPTIONS_COUNT       = new CountOptions();
    private static final CountOptions   OPTIONS_COUNT_EXIST = new CountOptions().limit(1);

    // ======

    private final EntityType<E> type;

    private final Snowflake snowflake;

    // ======

    /**
     * Entity cache.
     */
    private final MutableLongObjectMap<E> entities = new LongObjectHashMap<>();

    // ======

    public EntityManager(@Nonnull EntityType<E> type,
                         @Nonnull Snowflake snowflake) {
        this.type = type;
        this.snowflake = snowflake;
    }

    public EntityManager(@Nonnull EntityType<E> type) {
        this(type, CyNoodle.get().getSnowflake());
    }

    // ======

    @Nonnull
    public final EntityType<E> getType() {
        return this.type;
    }

    //

    @Nonnull
    public Snowflake getSnowflake() {
        return snowflake;
    }

    // ======

    @Nonnull
    public final Optional<E> get(long id) {

        // TODO improvements / thread safety

        // attempt to get the entity from the cache
        E entity = entities.get(id);

        if(entity == null) {
            // entity is not in the cache

            if(this.exists(id)) {
                // but exists, so create instance
                E instance = this.type.createInstance(this, id);

                this.entities.put(id, instance);

                entity = instance; // this is because suppressing warnings for assigning to existing variables is not possible
            }
            else return Optional.empty(); // entity does not exist
        }

        // update the cached entity before returning it
        this.update(id);

        return Optional.of(entity);
    }

    // ===

    @Nonnull
    @SuppressWarnings("UnstableApiUsage")
    public final LongStream streamIDs(@Nonnull Bson filter) throws EntityIOException {

        Stream<BsonDocument> stream;

        try {
            FindIterable<BsonDocument> find = collection().find(filter).projection(Projections.include(KEY_ID));
            stream = Streams.stream(find);
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB find command!", e);
        }

        // TODO in-stream exception handling for data format (BsonDataException) ???
        return stream
                .mapToLong(data -> data.getInt64(KEY_ID).longValue()).sorted(); // sort by ID (ascending);
    }

    @Nonnull
    public final LongStream streamIDs() throws EntityIOException {
        return this.streamIDs(DEFAULT_FILTER);
    }

    //

    @Nonnull
    public final Stream<E> stream(@Nonnull Bson filter) throws EntityIOException {
        return this.streamIDs(filter).mapToObj(id -> get(id).orElseThrow(() ->
                new IllegalStateException("ID Stream contained non-existent Entity ID: " + id)));
    }

    @Nonnull
    public final Stream<E> stream() throws EntityIOException  {
        return this.stream(DEFAULT_FILTER);
    }

    //

    @Nonnull
    public final List<E> list(@Nonnull Bson filter) throws EntityIOException {
        return this.stream(filter)
                .collect(Collectors.toList());
    }

    @Nonnull
    public final List<E> list() throws EntityIOException {
        return this.list(DEFAULT_FILTER);
    }

    //

    @Nonnull
    public Optional<E> first(@Nonnull Bson filter) throws EntityIOException {
        return this.stream(filter).findFirst();
    }

    @Nonnull
    public Optional<E> first() throws EntityIOException  {
        return this.first(DEFAULT_FILTER);
    }

    //

    @Nonnull
    public E firstOrCreate(@Nonnull Bson filter, @Nonnull Consumer<E> action) throws EntityIOException {
        Optional<E> result = this.first(filter);
        return result.orElseGet(() -> this.create(action));
    }

    //

    /**
     * Count the amount of Entities of which the saved state matches the given filter.
     * @param filter the filter
     * @return the amount of matched Entities
     * @throws EntityIOException if there was an IO issue with counting the Entities
     */
    public final long count(@Nonnull Bson filter) throws EntityIOException {
        try {
            return collection().countDocuments(filter, OPTIONS_COUNT);
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB count command!", e);
        }
    }

    /**
     * Count the amount of Entities.
     * @return the amount of Entities
     * @throws EntityIOException if there was an IO issue with counting the Entities
     */
    public final long count() throws EntityIOException {
        return count(DEFAULT_FILTER);
    }

    //

    public final boolean exists(@Nonnull Bson filter) throws EntityIOException {
        try {
            return collection().countDocuments(filter, OPTIONS_COUNT_EXIST) >= 1;
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB count command!", e);
        }
    }

    public final boolean exists(long id) throws EntityIOException {
        return this.exists(Filters.eq(KEY_ID, id));
    }

    public final boolean exists() throws EntityIOException {
        return this.exists(DEFAULT_FILTER);
    }

    // === CREATION ===

    /**
     * Create a new Entity.
     * The ID of the new Entity will be generated using the local snowflake algorithm.
     * @param action an action to perform on the Entity before persisting it
     * @return the new Entity.
     * @throws EntityIOException if there was an IO issue with persisting the new Entity
     */
    @Nonnull
    public final E create(@Nonnull Consumer<E> action) throws EntityIOException {

        // generate a new ID
        long id = this.snowflake.next();

        // create a new entity instance
        E instance = this.type.createInstance(this, id);

        // insert instance into the cache
        this.entities.put(id, instance);

        action.accept(instance);

        this.persist(id);

        // Note: IllegalStateException cannot be thrown here because we already cached it beforehand

        LOG.atFine().log("Created new Entity %s with ID %s.", this.type.getIdentifier(), id);

        return instance;
    }

    // TODO docs
    @Nonnull
    public final E create() throws EntityIOException {
        return this.create(e -> {});
    }

    // === DATA ===

    /**
     * <b>Persist</b> the cached Entity of the given ID (save the state in MongoDB).
     * The Entity does not need to exist in MongoDB beforehand.
     * @param id the Entity ID
     * @throws IllegalStateException if the Entity is not cached
     * @throws EntityIOException if there was an IO issue with persisting the Entity
     */
    public final void persist(long id)
            throws IllegalStateException, EntityIOException {

        E entity = this.entities.get(id);
        if(entity == null) throw new IllegalStateException("Entity " + id + " is not cached!");

        //

        BsonDocument data;

        try {
            data = entity.toBson().asBson();
        } catch (BsonDataException e) {
            throw new EntityIOException("Failed to create BSON from Entity state!", e);
        }

        // populate the data with the entity ID
        data.put(KEY_ID, new BsonInt64(id));

        //

        try {
            collection().replaceOne(Filters.eq(KEY_ID, id), data, OPTIONS_REPLACE);
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB replace command!", e);
        }
    }

    //

    /**
     * <b>Update</b> the cached Entity of the given ID (load the state from MongoDB).
     * The Entity needs to exist in MongoDB.
     * @param id the Entity ID
     * @throws IllegalStateException if the Entity is not cached
     * @throws NoSuchElementException if the Entity does not exist
     * @throws EntityIOException if there was an IO issue with updating the Entity
     */
    public final void update(long id)
            throws IllegalStateException, NoSuchElementException, EntityIOException {

        E entity = this.entities.get(id);
        if(entity == null) throw new IllegalStateException("Entity " + id + " is not cached!");

        //

        BsonDocument data;

        try {
            data = collection().find(Filters.eq(KEY_ID, id)).first();
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB find command!", e);
        }

        if(data == null) throw new NoSuchElementException("There is no Entity with ID " + id + "!");

        //

        try {
            entity.fromBson(FluentDocument.wrap(data));
        } catch (BsonDataException e) {
            throw new EntityIOException("Failed to load BSON into Entity state!", e);
        }
    }

    // === DELETION ===

    /**
     * <b>Delete</b> the Entity of the given ID (remove from cache if cached and delete the state from MongoDB).
     * @param id the Entity ID
     * @throws NoSuchElementException if the Entity does not exist
     * @throws EntityIOException if there was an IO issue with deleting the Entity
     */
    public final void delete(long id)
            throws NoSuchElementException, EntityIOException {

        E entity = this.entities.get(id);
        if(entity != null) this.entities.remove(id);

        //

        DeleteResult result;

        try {
            result = collection().deleteOne(Filters.eq(KEY_ID, id), OPTIONS_DELETE);
        } catch (MongoException e) {
            throw new EntityIOException("Exception while issuing MongoDB delete command!", e);
        }

        if(result.getDeletedCount() == 0)
            throw new NoSuchElementException("Nothing was deleted because there was no Entity with ID " + id + "!");
    }

    // === CACHE ===

    /**
     * <b>Discard</b> the Entity of the given ID (remove it from the cache) if its in the cache.
     * @param id the Entity ID
     */
    public final void discard(long id) {
        if(this.entities.containsKey(id))
            this.entities.remove(id);
    }

    // === MONGO ===

    /**
     * Return the MongoDB collection for the type used by this manager (using <code>system:mongo</code>).
     * @return the MongoDB collection
     */
    @Nonnull
    private MongoCollection<BsonDocument> collection() {

        // attempt to get system:mongo
        MongoModule module = (MongoModule) Module.get("system:mongo");

        MongoClient client = module.getClient();

        String database = "cynoodle-entities"; // TODO configurable

        return client
                .getDatabase(database)
                .getCollection(this.type.getCollection(), BsonDocument.class);
    }
}
