package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class UserNotFoundException extends AuthenticationException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
