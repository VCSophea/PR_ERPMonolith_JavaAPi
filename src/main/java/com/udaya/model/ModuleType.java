package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleType {

	private Long id;
	private String name;
	private Integer ordering;
	private Integer status; // 0: disabled, 1: enabled
}
