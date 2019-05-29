package com.egs.user.exception;

public class EmailAlreadyExistException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistException(final String email) {
        super("User already exists for given email.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
