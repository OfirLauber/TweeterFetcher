package il.ac.colman.cs.util;

import il.ac.colman.cs.ExtractedLink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
    private final Connection conn;

    public DataStorage() throws SQLException {
        String hostname = System.getProperty("config.rds.hostname");
        String port = System.getProperty("config.rds.port");
        String dbName = System.getProperty("config.rds.db.name");
        String username = System.getProperty("config.rds.username");
        String password = System.getProperty("config.rds.password");

        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", hostname, port, dbName, username, password);

        conn = DriverManager.getConnection(jdbcUrl);
    }

    /**
     * Add link to the database
     */
    public void addLink(ExtractedLink link, String track) {
        /*
        This is where we'll add our link
         */
    }

    /**
     * Search for a link
     *
     * @param query The query to search
     */
    public List<ExtractedLink> search(String query) {
        /*
        Search for query in the database and return the results
         */

        return null;
    }
}
