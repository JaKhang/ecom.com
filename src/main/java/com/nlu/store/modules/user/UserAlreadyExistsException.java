package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class UserAlreadyExistsException extends AuthenticationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
