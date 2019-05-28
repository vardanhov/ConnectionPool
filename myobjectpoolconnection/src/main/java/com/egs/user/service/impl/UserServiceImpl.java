package com.egs.user.service.impl;

import com.egs.connection.ConnectionManager;
import com.egs.connection.impl.ConnectionManagerImpl;
import com.egs.user.User;
import com.egs.user.exception.UserNotFoundException;
import com.egs.user.service.CreateUserRequest;
import com.egs.user.service.UpdateUserRequest;
import com.egs.user.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private static final String GET_ALL_USER = "SELECT * FROM user";

    private static final String DELETE_USER = "UPDATE user SET deleted = ? WHERE id = ? AND deleted IS NULL";

    private static final String GET_USER_BY_ID = "SELECT * FROM user WHERE id = ? AND deleted IS NULL";

    private static final String INSERT_USER = "INSERT INTO user (id, email, first_name, last_name, created) VALUES(?, ?, ?, ?, ?)";

    private static final String UPDATE_USER = "UPDATE user SET email = ?, first_name = ?, last_name = ?, updated = ? where id = ? AND deleted IS NULL";

    private final ConnectionManager connectionManager;

    public UserServiceImpl() {
        this.connectionManager = new ConnectionManagerImpl();
    }

    @Override
    public User create(final CreateUserRequest createUserRequest) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(INSERT_USER);

            final String newId = UUID.randomUUID().toString();
            final java.util.Date currentDate = new java.util.Date();

            preparedStatement.setString(1, newId);
            preparedStatement.setString(2, createUserRequest.getEmail());
            preparedStatement.setString(3, createUserRequest.getFirstName());
            preparedStatement.setString(4, createUserRequest.getLastName());
            preparedStatement.setDate(5, new Date(currentDate.getTime()));

            preparedStatement.executeUpdate();
            connection.commit();

            final User user = new User();

            user.setId(newId);
            user.setEmail(createUserRequest.getEmail());
            user.setFirstName(createUserRequest.getFirstName());
            user.setLastName(createUserRequest.getLastName());
            user.setCreated(currentDate);

            return user;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(null, preparedStatement, connection);
        }
    }

    @Override
    public User update(final UpdateUserRequest updateUserRequest) {
        final User user = getById(updateUserRequest.getId());
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(UPDATE_USER);

            final java.util.Date currentDate = new java.util.Date();

            preparedStatement.setString(1, updateUserRequest.getEmail());
            preparedStatement.setString(2, updateUserRequest.getFirstName());
            preparedStatement.setString(3, updateUserRequest.getLastName());
            preparedStatement.setDate(4, new Date(currentDate.getTime()));
            preparedStatement.setString(5, updateUserRequest.getId());

            preparedStatement.executeUpdate();
            connection.commit();

            final User updatedUser = new User();

            updatedUser.setId(updateUserRequest.getId());
            updatedUser.setEmail(updateUserRequest.getEmail());

            updatedUser.setFirstName(updateUserRequest.getFirstName());
            updatedUser.setLastName(updateUserRequest.getLastName());

            updatedUser.setCreated(user.getCreated());
            updatedUser.setUpdated(currentDate);

            return updatedUser;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(null, preparedStatement, connection);
        }
    }

    @Override
    public User findById(final String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(true);

            preparedStatement = connection.prepareStatement(GET_USER_BY_ID);
            preparedStatement.setString(1, id);

            resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? new User(resultSet) : null;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public User getById(final String id) {
        final User user = findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(true);

            preparedStatement = connection.prepareStatement(GET_ALL_USER);

            resultSet = preparedStatement.executeQuery();

            final List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                users.add(new User(resultSet));
            }
            return users;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public void delete(final String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(DELETE_USER);
            preparedStatement.setString(1, id);

            final int count = preparedStatement.executeUpdate();
            if (count == 0) {
                throw new UserNotFoundException(id);
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(null, preparedStatement, connection);
        }
    }

    private void closeAll(final ResultSet resultSet, final PreparedStatement preparedStatement, final Connection connection) {
        close(resultSet);
        close(preparedStatement);
        closeConnection(connection);
    }

    private void closeConnection(final Connection connection) {
        connectionManager.releaseConnection(connection);
        close(connection);
    }

    private static void close(final AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final Exception ex) {
            // nothing to do
        }
    }
}