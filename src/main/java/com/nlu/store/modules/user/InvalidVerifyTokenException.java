package com.nlu.store.modules.user;

public class InvalidVerifyTokenException extends AuthenticationException {

    public InvalidVerifyTokenException(String message) {
        super(message);
    }
}
