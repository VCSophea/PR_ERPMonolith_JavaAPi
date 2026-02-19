package com.udaya.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private Long moduleTypeId;
	private String name;
	private String type;
	private Boolean checked;
	@JsonIgnore
	private Integer ordering;
	@JsonIgnore
	private Integer status; // 0: disabled, 1: enabled
}
