/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.mongo;

import com.google.common.flogger.FluentLogger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cynoodle.core.BuildConfig;
import cynoodle.CyNoodle;
import cynoodle.module.MIdentifier;
import cynoodle.module.MSystem;
import cynoodle.module.Module;
import org.bson.BsonDocument;
import org.bson.BsonInt32;

import javax.annotation.Nonnull;

/**
 * This module controls all functionality for MongoDB, the database used by cynoodle.
 */
@MIdentifier("system:mongo")
@MSystem
// TODO improvements
public final class MongoModule extends Module {
    private MongoModule() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    //

    private MongoClient client = null;

    // ===

    @Override
    protected void start() {
        super.start();

        LOG.atInfo().log("Setting up MongoDB ...");

        // TODO temporary: get this from parameters rather than inline
        ConnectionString mongoConnection = new ConnectionString("mongodb://localhost");

        //

        LOG.atConfig().log("MongoDB connection: %s", mongoConnection);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(mongoConnection)
                .applicationName(BuildConfig.NAME + "-" + BuildConfig.VERSION)
                .build();


        // create the client

        this.client = MongoClients.create(mongoClientSettings);

        // ping the database

        LOG.atInfo().log("Connecting to MongoDB ...");

        try {
            this.client
                    .getDatabase(CyNoodle.DB_NAME)
                    .runCommand(new BsonDocument().append("ping", new BsonInt32(1)));
        } catch (MongoException e) {
            throw new RuntimeException("Initial database ping failed!", e); // TODO exception type
        }


    }

    @Override
    protected void shutdown() {
        super.shutdown();

        LOG.atInfo().log("Closing MongoDB client ...");

        this.client.close();
    }

    // ===

    @Nonnull
    public MongoClient getClient() {
        if(this.client == null) throw new IllegalStateException(this + " needs to be started first!");
        return this.client;
    }
}
