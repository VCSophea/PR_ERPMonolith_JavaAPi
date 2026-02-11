package com.udaya.exception;

import com.udaya.dto.ErrorDetails;
import com.udaya.util.TelegramUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    private final TelegramUtil telegramUtil;

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request.getRequestURI()));
    }

    // * Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "Validation Error", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        log.error("Exception processing request", ex);
        sendTelegramAlert(ex, request);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiError> handleGlobalException(GlobalException ex, HttpServletRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.valueOf(ex.getStatus()), ex.getMessage()));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private void sendTelegramAlert(Exception ex, HttpServletRequest request) {
        try {
            String username = "Anonymous";
            String headerUser = request.getHeader("X-User-Username");
            if (headerUser != null) {
                username = headerUser;
            }

            ErrorDetails error = ErrorDetails.builder()
                    .systemActivityId(UUID.randomUUID().toString().substring(0, 8))
                    .message(ex.getMessage())
                    .endpoint(request.getRequestURI())
                    .username(username)
                    .dateTime(LocalDateTime.now())
                    .build();

            telegramUtil.sendErrorNotification(error);
        } catch (Exception e) {
            log.warn("Failed to send alert", e);
        }
    }
}
