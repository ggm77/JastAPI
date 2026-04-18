package com.seohamin.jastapi.db;

import java.sql.Connection;

/**
 * An interface that defines the contract for providing and managing connection lifecycles.
 */
public interface ConnectionProvider {
    Connection getConnection();
    void releaseConnection(Connection connection);
}
