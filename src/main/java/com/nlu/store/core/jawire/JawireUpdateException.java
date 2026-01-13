package com.nlu.store.core.jawire;

public class JawireUpdateException extends RuntimeException {
    private int status;
    private String error;

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public JawireUpdateException(String message, int status, String error) {
        super(message);
        this.status = status;
        this.error = error;
    }

}
