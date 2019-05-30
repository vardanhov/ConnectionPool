package com.egs.user.service.impl;

import com.egs.user.User;
import com.egs.user.service.CreateUserRequest;
import com.egs.user.service.UpdateUserRequest;
import com.egs.user.service.UserService;

import java.util.List;

public class UserServiceProxyImpl implements UserService {

    private final UserService userService;

    public UserServiceProxyImpl() {
        this.userService = new UserServiceImpl();
    }

    @Override
    public User create(final CreateUserRequest createUserRequest) {
        return userService.create(createUserRequest);
    }

    @Override
    public User update(final UpdateUserRequest updateUserRequest) {
        return null;
    }

    @Override
    public User findById(final String id) {
        return null;
    }

    @Override
    public User getById(final String id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void delete(final String id) {

    }

    @Override
    public boolean existsByEmail(final String email) {
        return false;
    }

    @Override
    public int deleteAll() {
        return 0;
    }

    @Override
    public List<User> findBy(final List<String> ids) {
        return null;
    }

    @Override
    public int deleteAll(final List<String> ids) {
        return 0;
    }
}