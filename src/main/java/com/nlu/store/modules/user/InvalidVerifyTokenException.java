package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class InvalidVerifyTokenException extends AuthenticationException {

    public InvalidVerifyTokenException(String message) {
        super(message);
    }
}
