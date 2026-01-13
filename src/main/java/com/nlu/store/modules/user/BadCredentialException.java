package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class BadCredentialException extends AuthenticationException {
    public BadCredentialException(String message) {
        super(message);
    }
}
