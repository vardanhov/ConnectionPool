package com.egs.connection.impl;

import com.egs.connection.ConnectionManager;
import com.egs.connection.pool.impl.ConnectionPool;

import java.sql.Connection;

public class ConnectionManagerImpl implements ConnectionManager {

    private static final int MIN_SIZE = 1;

    private static final int MAX_SIZE = 2;

    private static final boolean AUTO_INIT = false;

    private final ConnectionPool connectionPool;

    public ConnectionManagerImpl() {
        this.connectionPool = new ConnectionPool(MIN_SIZE, MAX_SIZE, AUTO_INIT);
    }

    @Override
    public Connection getConnection() {
        return connectionPool.get();
    }

    @Override
    public void releaseConnection(final Connection connection) {
        connectionPool.set(connection);
    }
}