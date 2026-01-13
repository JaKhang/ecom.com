package com.nlu.store.modules.user;

import com.nlu.store.core.exceptions.AuthenticationException;

public class AlreadyVerifiedException extends AuthenticationException {

    public AlreadyVerifiedException(String message) {
        super(message);
    }
}
