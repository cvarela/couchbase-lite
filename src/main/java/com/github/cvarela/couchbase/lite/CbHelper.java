package com.github.cvarela.couchbase.lite;

import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogFileConfiguration;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.ReplicatorType;
import com.couchbase.lite.Scope;
import com.couchbase.lite.URLEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Set;

import static com.github.cvarela.couchbase.lite.Constants.REPLICATOR_PASSWORD;
import static com.github.cvarela.couchbase.lite.Constants.REPLICATOR_URL;
import static com.github.cvarela.couchbase.lite.Constants.REPLICATOR_USERNAME;

/**
 * Utility class containing helper methods for Couchbase Lite.
 *
 * @since 1.0
 */
public final class CbHelper {

    private CbHelper() {
        // utility class
    }

    /**
     * Configure the logs for the database.
     */
    public static void configureLogs() {

        // log file
        LogFileConfiguration LogCfg = new LogFileConfiguration("./logs");
        LogCfg.setMaxSize(10240);
        LogCfg.setMaxRotateCount(5);
        LogCfg.setUsePlaintext(false);
        Database.log.getFile().setConfig(LogCfg);
        Database.log.getFile().setLevel(LogLevel.VERBOSE);

        // console
        Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
        Database.log.getConsole().setLevel(LogLevel.ERROR);
    }

    /**
     * Create a replicator configuration.
     *
     * @param properties the properties containing the configuration values for the replicator
     * @param collection the collection to replicate
     * @return the replicator configuration object
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static ReplicatorConfiguration createReplicatorConfiguration(final Properties properties,
                                                                        final Collection collection)
        throws URISyntaxException
    {
        return new ReplicatorConfiguration(new URLEndpoint(new URI(properties.getProperty(REPLICATOR_URL))))
            .addCollection(collection, null)
            .setContinuous(false)
            .setType(ReplicatorType.PUSH_AND_PULL)
            .setAuthenticator(
                new BasicAuthenticator(properties.getProperty(REPLICATOR_USERNAME),
                    properties.getProperty(REPLICATOR_PASSWORD).toCharArray()));
    }

    /**
     * Get or create a collection in the database.
     *
     * @param out           the output stream
     * @param database      the database
     * @param collectionNme the collection name
     * @param scopeName     the scope name
     * @return the collection
     * @throws CouchbaseLiteException if an error occurs
     */
    public static Collection getOrCreateCollection(Database database, String collectionNme, String scopeName)
        throws CouchbaseLiteException
    {
        Collection collection = database.getCollection(collectionNme, scopeName);
        if (null == collection) {
            collection = database.createCollection(collectionNme, scopeName);
            System.out.format("Collection created: %s%n", collection);
        } else {
            System.out.format("Collection already exists: %s%n", collection);
        }
        return collection;
    }

    /**
     * Print the collections in the database.
     *
     * @param database the database
     * @return the string representation of the collections in the database
     * @throws CouchbaseLiteException if an error occurs while accessing the database
     */
    public static String printCollectionsInDatabase(Database database) throws CouchbaseLiteException {
        final Set<Scope> scopes = database.getScopes();

        StringBuilder buffer = new StringBuilder();
        buffer.append("Database: ").append(database.getName());
        for (Scope scope : scopes) {
            buffer.append("\n    * Scope: ").append(scope.getName());
            final Set<Collection> collections = scope.getCollections();
            for (Collection collection : collections) {
                buffer.append("\n        - Collection: ").append(collection.getName());
            }
        }
        return buffer.toString();
    }

    /**
     * Stop and close the replicator and remove the listener token.
     *
     * @param replicator    the replicator to stop and close
     * @param listenerToken the listener token to remove
     */
    public static void stopAndCloseReplicator(Replicator replicator, ListenerToken listenerToken) {

        if (null != listenerToken) {
            listenerToken.remove();
        }

        if (null != replicator) {
            replicator.stop();
            replicator.close();
        }
    }
}
