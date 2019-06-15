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

import com.google.common.annotations.Beta;
import com.google.common.flogger.FluentLogger;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import cynoodle.CyNoodle;
import cynoodle.module.Module;
import cynoodle.mongodb.MongoModule;
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
