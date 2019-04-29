/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.entities;

import com.google.common.annotations.Beta;
import com.google.common.flogger.FluentLogger;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import cynoodle.CyNoodle;
import cynoodle.module.Module;
import cynoodle.mongo.MongoModule;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Beta
public final class EntityWatcher {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ======

    private final MongoModule module = Module.get(MongoModule.class);

    // ===

    private final Map<String, EntityManager<? extends Entity>> managers = new HashMap<>();

    //

    private MongoCursor<ChangeStreamDocument<BsonDocument>> cursor = null;
    private Thread watcherThread = null;

    // ===

    void register(@Nonnull EntityManager<? extends Entity> manager) {
        this.managers.put(manager.getType().getCollection(), manager);
    }

    // ===

    public void start() {
        if(cursor == null) {

            LOG.atFine().log("Starting EntityWatcher");

            // acquire change stream
            cursor = module.getClient().getDatabase(CyNoodle.DB_NAME)
                    .watch(BsonDocument.class).fullDocument(FullDocument.UPDATE_LOOKUP)
                    .iterator();

            // start watching
            watcherThread = new Thread(this::watch, "EntityWatcherThread");

            watcherThread.start();

        }
        else throw new IllegalStateException("EntityWatcher was already started!");
    }

    public void close() {
        if(cursor != null) {

            LOG.atFine().log("Closing EntityWatcher");

            this.cursor.close();
            this.watcherThread.interrupt();
        }
        else throw new IllegalStateException("EntityWatcher was never started!");
    }

    // ===

    private void watch() {
        while (cursor.hasNext() && !Thread.currentThread().isInterrupted()) {

            ChangeStreamDocument<BsonDocument> content = cursor.next();

            BsonDocument document = content.getFullDocument();
            if(document == null) continue;

            String collection = content.getNamespaceDocument()
                    .getString("coll").getValue();
            long id = document
                    .getInt64(EntityManager.KEY_ID).longValue();

            //

            EntityManager<? extends Entity> manager = this.managers.get(collection);
            if(manager == null) continue;

            //

            manager.updateWatched(id);
        }
    }
}
