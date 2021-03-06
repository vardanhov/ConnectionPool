package com.egs.user.service.impl;

import com.egs.connection.ConnectionManager;
import com.egs.connection.impl.ConnectionManagerImpl;
import com.egs.user.User;
import com.egs.user.exception.EmailAlreadyExistException;
import com.egs.user.exception.UserNotFoundException;
import com.egs.user.service.CreateUserRequest;
import com.egs.user.service.UpdateUserRequest;
import com.egs.user.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private static final String GET_ALL_USER = "SELECT * FROM user";

    private static final String DELETE_USER = "UPDATE user SET deleted = ? WHERE id = ? AND deleted IS NULL";

    private static final String GET_USER_BY_ID = "SELECT * FROM user WHERE id = ? AND deleted IS NULL";

    private static final String INSERT_USER = "INSERT INTO user (id, email, first_name, last_name, created) VALUES(?, ?, ?, ?, ?)";

    private static final String UPDATE_USER = "UPDATE user SET email = ?, first_name = ?, last_name = ?, updated = ? where id = ? AND deleted IS NULL";

    private static final String GET_USER_BY_EMAIL = "SELECT * FROM user WHERE email = ? AND deleted IS NULL";

    private static final String DELETE_ALL_USERS = "DELETE FROM USER";

    private final ConnectionManager connectionManager;

    UserServiceImpl() {
        this.connectionManager = new ConnectionManagerImpl();
    }

    @Override
    public User create(final CreateUserRequest createUserRequest) {
        checkIfExistsByEmail(createUserRequest.getEmail());
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_USER);

            final String newId = UUID.randomUUID().toString();
            final Date currentDate = new Date();

            preparedStatement.setString(1, newId);
            preparedStatement.setString(2, createUserRequest.getEmail());
            preparedStatement.setString(3, createUserRequest.getFirstName());
            preparedStatement.setString(4, createUserRequest.getLastName());
            preparedStatement.setTimestamp(5, new Timestamp(currentDate.getTime()));

            preparedStatement.executeUpdate();

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
            closeAll(preparedStatement, connection);
        }
    }

    @Override
    public User update(final UpdateUserRequest updateUserRequest) {
        Connection connection = null;
        final User user = getById(updateUserRequest.getId());
        if (!user.getEmail().equals(updateUserRequest.getEmail())) {
            checkIfExistsByEmail(updateUserRequest.getEmail());
        }

        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();

            preparedStatement = connection.prepareStatement(UPDATE_USER);
            final java.util.Date currentDate = new java.util.Date();

            preparedStatement.setString(1, updateUserRequest.getEmail());
            preparedStatement.setString(2, updateUserRequest.getFirstName());
            preparedStatement.setString(3, updateUserRequest.getLastName());
            preparedStatement.setTimestamp(4, new Timestamp(currentDate.getTime()));
            preparedStatement.setString(5, updateUserRequest.getId());

            preparedStatement.executeUpdate();

            final User result = new User();
            result.setId(updateUserRequest.getId());
            result.setEmail(updateUserRequest.getEmail());
            result.setFirstName(updateUserRequest.getFirstName());
            result.setLastName(updateUserRequest.getLastName());
            result.setCreated(user.getCreated());
            result.setUpdated(currentDate);

            return result;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(preparedStatement, connection);
        }
    }

    @Override
    public User findById(final String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();

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
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();

            statement = connection.createStatement();

            resultSet = statement.executeQuery(GET_ALL_USER);

            List<User> usersFrom = getUsersFrom(resultSet);
            return usersFrom;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(resultSet, statement, connection);
        }
    }

    @Override
    public void delete(final String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            final java.util.Date currentDate = new java.util.Date();
            connection = connectionManager.getConnection();

            preparedStatement = connection.prepareStatement(DELETE_USER);
            preparedStatement.setTimestamp(1, new Timestamp(currentDate.getTime()));
            preparedStatement.setString(2, id);

            final int count = preparedStatement.executeUpdate();
            if (count == 0) {
                throw new UserNotFoundException(id);
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(preparedStatement, connection);
        }
    }

    @Override
    public boolean existsByEmail(final String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();

            preparedStatement = connection.prepareStatement(GET_USER_BY_EMAIL);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public int deleteAll() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectionManager.getConnection();

            statement = connection.createStatement();
            return statement.executeUpdate(DELETE_ALL_USERS);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(statement, connection);
        }
    }

    @Override
    public List<User> findBy(final List<String> ids) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();

            statement = connection.createStatement();
            final String query = "SELECT * FROM user WHERE id in " + getIn(ids) + " AND deleted IS NULL";
            resultSet = statement.executeQuery(query);

            return getUsersFrom(resultSet);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(resultSet, statement, connection);
        }
    }

    @Override
    public int deleteAll(final List<String> ids) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            final String deleteUsers = "UPDATE user SET deleted = ? WHERE id in " + getIn(ids) + " AND deleted IS NULL";
            connection = connectionManager.getConnection();

            preparedStatement = connection.prepareStatement(deleteUsers);
            preparedStatement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));

            return preparedStatement.executeUpdate();
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(preparedStatement, connection);
        }
    }

    private void checkIfExistsByEmail(final String email) {
        if (existsByEmail(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private void closeAll(final Statement statement, final Connection connection) {
        closeAll(null, statement, connection);
    }

    private void closeAll(final ResultSet resultSet, final Statement statement, final Connection connection) {
        close(resultSet);
        close(statement);
        connectionManager.releaseConnection(connection);
    }

    private static List<User> getUsersFrom(final ResultSet resultSet) throws SQLException {
        final List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(new User(resultSet));
        }
        return users;
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

    private static String getIn(final List<String> ids) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");

        final int size = ids.size();
        for (int idx = 0; idx < size; idx++) {
            builder.append("'").append(ids.get(idx)).append("'");
            if (idx != size - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}