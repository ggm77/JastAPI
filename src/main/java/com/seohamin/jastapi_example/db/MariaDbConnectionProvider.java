package com.seohamin.jastapi_example.db;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi.db.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class MariaDbConnectionProvider implements ConnectionProvider {

    // DB 연결 정보
    private final static String URL = "jdbc:mariadb://localhost:3306/jastapi_example";
    private final static String USER = "jastapi";
    private final static String PASSWORD = "1234";

    // DB 커넥션 얻는 메서드
    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.err.println("[ERROR] MariaDB 연결 실패: " + ex.getMessage());
            return null;
        }
    }

    // DB 커넥션 끊는 메서드
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
