package com.egs.connection.pool.impl;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionPool extends ObjectPoolImpl<Connection> {

    private static final String USERNAME = "root";

    private static final String PASSWORD = "root";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user1_db?allowPublicKeyRetrieval=true&useSSL=false";

    public ConnectionPool(int minSize, int maxSize, boolean autoInit) {
        super(minSize, maxSize, autoInit);
    }

    @Override
    protected Connection create() {
        try {
            Class.forName(JDBC_DRIVER);
            return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (final Exception ex) {
            throw new RuntimeException("Unable to get connection.", ex);
        }
    }
}