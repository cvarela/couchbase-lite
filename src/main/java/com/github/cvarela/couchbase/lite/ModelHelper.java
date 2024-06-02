package com.github.cvarela.couchbase.lite;

import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDictionary;
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

    private ModelHelper() {
        // utility class
    }

    /**
     * Create a new airline document for Iberia.
     *
     * @return the airline document
     */
    public static MutableDocument getIberiaAirline() {
        return new MutableDocument("airline_20000")
            .setString("type", "airline")
            .setInt("id", 20000)
            .setString("callsign", "IBERIA")
            .setValue("iata", "IB")
            .setValue("icao", "IBE")
            .setString("name", "Iberia")
            .setValue("fleet", new MutableDictionary()
                .setInt("airbus", 18)
                .setInt("boeing", 2)
                .setInt("embraer", 1)
                .setInt("mcdonnell-douglas", 1)
            )
            .setDate("updated", new java.util.Date())
            .setString("base", "MAD")
            .setString("country", "Spain");
    }

    /**
     * Create a new airline document for Vueling.
     *
     * @return the airline document
     */
    public static MutableDocument getVuelingAirline() {
        return new MutableDocument("airline_20001")
            .setString("type", "airline")
            .setInt("id", 20001)
            .setString("callsign", "VUELING")
            .setValue("iata", "VY")
            .setValue("icao", "VLG")
            .setString("name", "Vueling Airlines")
            .setValue("fleet", new MutableDictionary()
                .setInt("airbus", 22)
            )
            .setDate("updated", new java.util.Date())
            .setString("base", "BCN")
            .setString("country", "Spain");
    }

    /**
     * Query the collection for airlines in a specific country and return the results as a string.
     * This method is used to demonstrate how to query a collection and process the results.
     * In a real-world application, the query would be more complex and the results would be processed differently.
     *
     * @param collection the collection to query
     * @param country    the country to query
     */
    public static String printAirlinesInCountry(Collection collection, String country) throws CouchbaseLiteException {

        Query query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("country").equalTo(Expression.string(country)));

        StringBuffer buffer = new StringBuffer();
        buffer.append("List of airlines in ").append(country);
        try (ResultSet rs = query.execute()) {
            rs.allResults().forEach(r -> buffer.append("\n    * ").append(r.toMap()));
        }
        return buffer.toString();
    }
}
