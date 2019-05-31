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
        return userService.update(updateUserRequest);
    }

    @Override
    public User findById(final String id) {
        return userService.findById(id);
    }

    @Override
    public User getById(final String id) {
        return userService.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userService.getAll();
    }

    @Override
    public void delete(final String id) {
        userService.delete(id);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return userService.existsByEmail(email);
    }

    @Override
    public int deleteAll() {
        return userService.deleteAll();
    }

    @Override
    public List<User> findBy(final List<String> ids) {
        return userService.findBy(ids);
    }

    @Override
    public int deleteAll(final List<String> ids) {
        return userService.deleteAll(ids);
    }
}