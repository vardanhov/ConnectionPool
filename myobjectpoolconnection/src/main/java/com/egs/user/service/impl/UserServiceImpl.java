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

    public UserServiceImpl() {
        this.connectionManager = new ConnectionManagerImpl();
    }

    @Override
    public User create(final CreateUserRequest createUserRequest) {

        checkIfExistsByEmail(createUserRequest.getEmail());

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(false);

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
            closeAll(null, preparedStatement, connection);
        }
    }

    @Override
    public User update(final UpdateUserRequest updateUserRequest) {
        final User user = getById(updateUserRequest.getId());
        if (!user.getEmail().equals(updateUserRequest.getEmail())) {
            checkIfExistsByEmail(updateUserRequest.getEmail());
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(false);

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
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(true);

            statement = connection.createStatement();

            resultSet = statement.executeQuery(GET_ALL_USER);

            final List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                users.add(new User(resultSet));
            }
            return users;
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
            connection = connectionManager.getConnection();
            connection.setReadOnly(false);

            final java.util.Date currentDate = new java.util.Date();

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
            closeAll(null, preparedStatement, connection);
        }
    }

    @Override
    public boolean existsByEmail(final String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(true);

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
    public void deleteAll() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectionManager.getConnection();
            connection.setReadOnly(false);

            statement = connection.createStatement();

            statement.executeUpdate(DELETE_ALL_USERS);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeAll(null, statement, connection);
        }
    }

    @Override
    public List<User> findBy(final List<String> ids) {
        return null;
    }

    @Override
    public void deleteAll(final List<String> ids) {

    }

    private void closeAll(final ResultSet resultSet, final Statement preparedStatement, final Connection connection) {
        close(resultSet);
        close(preparedStatement);
        connectionManager.releaseConnection(connection);
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

    private void checkIfExistsByEmail(final String email) {
        if (existsByEmail(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }
}