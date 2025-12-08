package com.nlu.store.modules.user;

public class ExpiredVerifyTokenException extends AuthenticationException {

    public ExpiredVerifyTokenException(String message) {
        super(message);
    }
}
