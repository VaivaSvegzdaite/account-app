package com.example.sas.exceptions;

public class NotFoundAccountException extends RuntimeException {

    public NotFoundAccountException(String message) {
        super(message);
    }
}
