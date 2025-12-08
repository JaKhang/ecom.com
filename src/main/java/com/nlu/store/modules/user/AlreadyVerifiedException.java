package com.nlu.store.modules.user;

public class AlreadyVerifiedException extends AuthenticationException {

    public AlreadyVerifiedException(String message) {
        super(message);
    }
}
