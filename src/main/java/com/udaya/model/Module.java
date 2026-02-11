package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

	private Long id;
	private Long moduleTypeId;
	private String name;
	private Integer ordering;
	private Integer status; // 0: disabled, 1: enabled
}
