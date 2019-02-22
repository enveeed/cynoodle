/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.mongo;

import com.google.common.flogger.FluentLogger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cynoodle.core.BuildConfig;
import cynoodle.core.CyNoodle;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MSystem;
import cynoodle.core.module.Module;
import cynoodle.core.module.ModuleStartException;
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

        // TODO temporary: get this from config file rather than start parameters
        ConnectionString mongoConnection = CyNoodle.get().getParameters().getMongoConnection();

        //

        LOG.atConfig().log("MongoDB connection: %s", mongoConnection);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(mongoConnection)
                .applicationName(BuildConfig.NAME + "-" + BuildConfig.VERSION)
                .build();


        // create the client

        this.client = MongoClients.create(mongoClientSettings);

        // ping the database

        LOG.atConfig().log("Connecting to MongoDB ...");

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
