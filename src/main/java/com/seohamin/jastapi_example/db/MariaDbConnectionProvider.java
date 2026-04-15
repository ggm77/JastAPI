package com.seohamin.jastapi_example.db;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.db.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provides the connection with MariaDB.
 */
@Component
public class MariaDbConnectionProvider implements ConnectionProvider {

    // DB 연결 정보 | DB connection info
    private final static String URL = "jdbc:mariadb://localhost:3306/jastapi_example";
    private final static String USER = "jastapi";
    private final static String PASSWORD = "1234";

    /**
     * Returns the connection of DB.
     * @return connection with DB.
     */
    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.err.println("[ERROR] Failed to connect MariaDB: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Releases the connection with DB.
     * @param connection connection to release.
     */
    @Override
    public void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println("[ERROR] Error occurred while release the connection with MariaDB: " + ex.getMessage());
        }
    }
}
