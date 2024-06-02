package com.github.cvarela.couchbase.lite;

import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Replicator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static com.github.cvarela.couchbase.lite.Constants.COLLECTION_NAME;
import static com.github.cvarela.couchbase.lite.Constants.DATABASE_NAME;
import static com.github.cvarela.couchbase.lite.Constants.SCOPE_NAME;

public class DemoLite {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.format(
                    "Usage: java -cp $CLASSPATH %s <path-to-configuration-file.properties>", DemoLite.class.getName());
                System.exit(1);
            }
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(args[0])));
            new DemoLite().run(properties);
        } catch (CouchbaseLiteException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void run(Properties properties) throws CouchbaseLiteException, URISyntaxException {

        System.out.format("Initializing Couchbase Lite (local database: %s) ...%n", DATABASE_NAME);

        CouchbaseLite.init();
        CbHelper.configureLogs();

        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setDirectory(".couchbase-db");

        Replicator replicator = null;
        ListenerToken listenerToken = null;

        try (Database database = new Database(DATABASE_NAME, dbConfig)) {

            // show all collections in the database prior to creating a new one
            System.out.println(CbHelper.printCollectionsInDatabase(database));

            // create a new collection in the database if it does not exist yet or get it if it does exist already
            Collection collection = CbHelper.getOrCreateCollection(database,
                properties.getProperty(COLLECTION_NAME),
                properties.getProperty(SCOPE_NAME));

            // show all collections in the database after creating a new one
            System.out.println(CbHelper.printCollectionsInDatabase(database));

            // save some documents in the collection to be replicated to the remote endpoint later on (see below)
            collection.save(ModelHelper.getIberiaAirline());
            collection.save(ModelHelper.getVuelingAirline());

            // create a replicator to push and pull data to and from the remote endpoint
            replicator = new Replicator(CbHelper.createReplicatorConfiguration(properties, collection));

            // Listen to replicator change events
            listenerToken = replicator.addChangeListener(change -> {
                System.out.format("Replicator state: %s%n  |---> Replicator: %s%n  |---> Status: %s%n",
                    change.getStatus().getActivityLevel(), change.getReplicator(), change.getStatus());
            });

            // Start replication
            replicator.start(false);

            // wait for the replication to complete
            Thread.sleep(30_000);

            // show all airlines in Spain and the United States
            System.out.println(ModelHelper.printAirlinesInCountry(collection, "Spain"));
            System.out.println(ModelHelper.printAirlinesInCountry(collection, "United States"));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            CbHelper.stopAndCloseReplicator(replicator, listenerToken);
        }
    }
}
