package com.udaya.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();

    // * Static factory for success response
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().success(true).message("Success").data(data).build();
    }

    // * Static factory for error response
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).build();
    }
}
