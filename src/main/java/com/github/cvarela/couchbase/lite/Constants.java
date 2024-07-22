package com.github.cvarela.couchbase.lite;

/**
 * Constants used in the application. The values are read from a configuration file (properties).
 *
 * @since 1.0
 */
public class Constants {

    public static final String STORE_ID = "store.id";
    public static final String SCOPE_NAME = "scope.name";
    public static final String COLLECTION_NAME = "collection.name";
    public static final String REPLICATOR_URL = "replicator.url";
    public static final String REPLICATOR_USERNAME = "replicator.username";
    public static final String REPLICATOR_PASSWORD = "replicator.password";

    private Constants() {
        // utility class
    }
}
