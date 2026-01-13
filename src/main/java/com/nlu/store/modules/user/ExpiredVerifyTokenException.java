package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class ExpiredVerifyTokenException extends AuthenticationException {

    public ExpiredVerifyTokenException(String message) {
        super(message);
    }
}
