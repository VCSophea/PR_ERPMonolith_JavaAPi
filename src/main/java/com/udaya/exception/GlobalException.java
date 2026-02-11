package com.udaya.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final int status;

    // * Custom Exception with HTTP Status
    public GlobalException(String message, int status) {
        super(message);
        this.status = status;
    }
}
