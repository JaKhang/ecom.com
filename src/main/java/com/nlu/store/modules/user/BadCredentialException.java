package com.nlu.store.modules.user;

public class BadCredentialException extends AuthenticationException {
    public BadCredentialException(String message) {
        super(message);
    }
}
