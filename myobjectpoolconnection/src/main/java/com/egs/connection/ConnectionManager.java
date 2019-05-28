package com.egs.connection;

import java.sql.Connection;

public interface ConnectionManager {

    Connection getConnection();

    void releaseConnection(Connection connection);
}