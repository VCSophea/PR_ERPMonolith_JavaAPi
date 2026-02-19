package com.udaya.dto.common;

import lombok.Data;

@Data
public class BaseSearchRequest {
	private int page = 1;
	private int size = 10;
	private String keyword;
}
