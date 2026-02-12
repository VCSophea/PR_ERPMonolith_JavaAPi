package com.udaya.exception;

import com.udaya.dto.ErrorDetails;
import com.udaya.util.TelegramUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

	private final TelegramUtil telegramUtil;

	// * Handle No Resource Found
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiError> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request.getRequestURI());
	}

	// * Handle Validation Errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> errors = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
		return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors, request.getRequestURI());
	}

	// * Handle General Exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
		log.error("Exception processing request", ex);
		sendTelegramAlert(ex, request);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), List.of(ex.getMessage()), request.getRequestURI());
	}

	// * Handle Global Custom Exceptions
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiError> handleGlobalException(GlobalException ex, HttpServletRequest request) {
		return buildResponse(HttpStatus.valueOf(ex.getStatus()), ex.getMessage(), List.of(ex.getMessage()), request.getRequestURI());
	}

	private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, List<String> errors, String path) {
		return new ResponseEntity<>(ApiError.builder().status(status).message(message).errors(errors).path(path).build(), status);
	}

	private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, String error, String path) {
		return buildResponse(status, message, List.of(error), path);
	}

	// * Send Telegram Alert
	private void sendTelegramAlert(Exception ex, HttpServletRequest request) {
		try {
			telegramUtil.sendErrorNotification(ErrorDetails.builder()
			                                               .systemActivityId(UUID.randomUUID().toString())
			                                               .message(ex.getMessage())
			                                               .endpoint(request.getRequestURI())
			                                               .username(request.getHeader("X-User-Username") != null ? request.getHeader("X-User-Username") : "Anonymous")
			                                               .dateTime(LocalDateTime.now())
			                                               .build());
		} catch (Exception e) {
			log.warn("Failed to send alert", e);
		}
	}
}
