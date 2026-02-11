package com.udaya.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDetails {
	private String systemActivityId;
	private String message;
	private String endpoint;
	private String username;
	private LocalDateTime dateTime;
}
