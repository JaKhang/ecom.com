package com.nlu.store.modules.user;

public class UserAlreadyExistsException extends AuthenticationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
