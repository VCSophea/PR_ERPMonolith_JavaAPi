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
	private int status;
	private String message;
	private T data;
	private Pagination pagination;

	public static <T> BaseResponse<T> success(T data) {
		return BaseResponse.<T>builder().status(HttpStatus.OK.value()).message("Success").data(data).build();
	}

	public static <T> BaseResponse<T> success(T data, Pagination pagination) {
		return BaseResponse.<T>builder().status(HttpStatus.OK.value()).message("Success").data(data).pagination(pagination).build();
	}
}
