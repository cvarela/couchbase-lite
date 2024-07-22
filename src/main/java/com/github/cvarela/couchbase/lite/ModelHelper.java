package com.github.cvarela.couchbase.lite;

import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

/**
 * A helper class for the model objects in the application. It provides methods to create and manipulate model objects.
 * In a real-world application, this class would contain more methods to create and manipulate model objects.
 *
 * @since 1.0
 */
public class ModelHelper {

    private static final String STOCK_TYPE = "stock";
    private static final String TYPE_PROPERTY = "type";

    private ModelHelper() {
        // utility class
    }

    /**
     * Generates a stock document for the given product and store.
     *
     * @param productId the product identifier
     * @param storeId   the store identifier
     * @return the stock document
     */
    public static MutableDocument generateStockDocument(final int productId, final int storeId) {
        return new MutableDocument("store_" + storeId + "_product_" + productId)
            .setArray("channels", new MutableArray().addString("store-" + storeId))
            .setString(TYPE_PROPERTY, STOCK_TYPE)
            .setInt("storeId", storeId)
            .setInt("productId", productId)
            .setInt("quantity", storeId * productId)
            .setDate("updated", new java.util.Date());
    }

    /**
     * Prints the stock information in the given collection.
     *
     * @param collection the collection to query
     */
    public static String printStock(Collection collection) throws CouchbaseLiteException {

        Query query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property(TYPE_PROPERTY).equalTo(Expression.string(STOCK_TYPE)));

        StringBuffer buffer = new StringBuffer();
        buffer.append("Stock info");
        try (ResultSet rs = query.execute()) {
            rs.allResults().forEach(r -> buffer.append("\n    * ").append(r.toMap()));
        }
        return buffer.toString();
    }
}
