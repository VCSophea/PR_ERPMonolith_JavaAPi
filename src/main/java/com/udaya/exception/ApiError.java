package com.udaya.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private List<String> errors;
    private String path;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this(status);
        this.message = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this(status);
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, String error) {
        this(status);
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public ApiError(HttpStatus status, String message, List<String> errors) {
        this(status);
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error, String path) {
        this(status, message, error);
        this.path = path;
    }
}
