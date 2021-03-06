package il.ac.colman.cs.util;

import com.mysql.cj.protocol.Resultset;
import il.ac.colman.cs.ExtractedLink;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.sql.DriverManager.getConnection;

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

        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        conn = DriverManager.getConnection(jdbcUrl);
    }

    /**
     * Add link to the database
     */
    public void addLink(ExtractedLink link, String track) {
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO links (link, track, content, title, description, screenshotURL, date) " +
                            "VALUES(?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, link.getLink());
            statement.setString(2, track);
            statement.setString(3, link.getContent());
            statement.setString(4, link.getTitle());
            statement.setString(5, link.getDescription());
            statement.setString(6, link.getScreenshotURL());
            statement.setDate(7, new Date(link.getDate().getTime()));

            statement.execute();
            statement.close();

            ResultSet rowsNumerResult = conn.prepareStatement("select count(*) as 'row_count' from links").executeQuery();

            while(rowsNumerResult.next()){
                if(rowsNumerResult.getInt("row_count") > 1000){
                    // delete older links
                    this.deleteIrrelevantLinks();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search for a link
     *
     * @param query The query to search
     */
    public List<ExtractedLink> search(String query) {

        List<ExtractedLink> searchResults = new ArrayList<ExtractedLink>();
        String queryForSearch = "SELECT * FROM links WHERE content like '%" + query + "%' OR title like '%" + query + "%' OR description like '%" + query + "%'";

        try {
            ResultSet result = conn.prepareStatement(queryForSearch).executeQuery();
            while(result.next()){
               searchResults.add(new ExtractedLink(
                        result.getString("link"),
                        result.getDate("date"),
                        result.getString("content"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getString("screenshotURL")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults;
    }

    public void deleteIrrelevantLinks(){
        try {
            conn.prepareStatement("delete from links order by date asc limit 2 ").execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
