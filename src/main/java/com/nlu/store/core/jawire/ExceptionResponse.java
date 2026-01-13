package com.nlu.store.core.jawire;

public class ExceptionResponse{
    int status;
    String message;
    String error;

    public ExceptionResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
