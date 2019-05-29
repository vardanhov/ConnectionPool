package com.egs.user.service;

import com.egs.user.User;

import java.util.List;

public interface UserService {

    User create(CreateUserRequest createUserRequest);

    User update(UpdateUserRequest updateUserRequest);

    User findById(String id);

    User getById(String id);

    List<User> getAll();

    void delete(String id);

    boolean existsByEmail(String email);

    void deleteAll();

    List<User> findBy(List<String> ids);

    void deleteAll(List<String> ids);
}