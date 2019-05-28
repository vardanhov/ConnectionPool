package com.egs.user.service;

public class UpdateUserRequest {

    private final String id;

    private final String email;

    private final String firstName;

    private final String lastName;

    public UpdateUserRequest(final String id, final String email, final String firstName, final String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}