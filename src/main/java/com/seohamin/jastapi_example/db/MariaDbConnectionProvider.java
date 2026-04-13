package com.seohamin.jastapi_example.db;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi.db.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class MariaDbConnectionProvider implements ConnectionProvider {

    private final static String URL = "jdbc:mariadb://localhost:3306/jastapi";
    private final static String USER = "user";
    private final static String PASSWORD = "1234";

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.err.println("[ERROR] MariaDB 연결 실패: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println("[ERROR] 연결 해제 중 오류 발생: " + ex.getMessage());
        }
    }
}
