package com.seohamin.jastapi.db;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
    void releaseConnection(Connection connection);
}
