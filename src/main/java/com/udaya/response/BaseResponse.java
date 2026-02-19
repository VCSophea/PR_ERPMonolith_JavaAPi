package com.udaya.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	private boolean success;
	private int status;
	private String message;
	private T data;
	private Pagination pagination;
	private java.util.List<String> errors; // Added errors field
	@Builder.Default
	private java.time.LocalDateTime timestamp = java.time.LocalDateTime.now();

	public static <T> BaseResponse<T> success(T data) {
		return BaseResponse.<T>builder().success(true).status(HttpStatus.OK.value()).message("Success").data(data).build();
	}

	public static <T> BaseResponse<T> success(T data, Pagination pagination) {
		return BaseResponse.<T>builder().success(true).status(HttpStatus.OK.value()).message("Success").data(data).pagination(pagination).build();
	}

	public static <T> BaseResponse<T> error(HttpStatus status, String message) {
		return BaseResponse.<T>builder().success(false).status(status.value()).message(message).build();
	}

	public static <T> BaseResponse<T> error(HttpStatus status, String message, java.util.List<String> errors) {
		return BaseResponse.<T>builder().success(false).status(status.value()).message(message).errors(errors).build();
	}
}
