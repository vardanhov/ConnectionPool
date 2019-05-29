package com.egs.user.exception;

public class UserNotFoundException extends RuntimeException {

    private final String identifier;

    public UserNotFoundException(final String identifier) {
        super("User not found.");
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}